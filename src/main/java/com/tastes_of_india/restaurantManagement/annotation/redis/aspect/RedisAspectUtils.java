package com.tastes_of_india.restaurantManagement.annotation.redis.aspect;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.tastes_of_india.restaurantManagement.repository.GenericRedisRepository;
import com.tastes_of_india.restaurantManagement.utils.DynamicPropertyFilter;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RedisAspectUtils {

    private static final Logger Log= LoggerFactory.getLogger(RedisAspectUtils.class);

    private RedisAspectUtils(){}

    public static Object getValue(String arg,Object[] args,Object result) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(arg.startsWith("$")){
            return getValueFromResult(arg,result);
        }else if(arg.startsWith("#")){
            return getValueFromParameters(arg,args);
        }
        return arg;
    }

    public static Object read(GenericRedisRepository genericRedisRepository,String key,String id,Class<?> actualClas,Class<?> returnType,ObjectMapper objectMapper) throws JsonProcessingException {
        String value=genericRedisRepository.get(key,id);

        if(value!=null){
            return getValueToReturn(value,actualClas,returnType,objectMapper);
        }

        return null;
    }

    public static Long getTTL(Object result,String expiryPath,Long expiryAdditionInSeconds,Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(expiryPath.isEmpty()){
            return null;
        }
        Object expiryPathDateTime=null;

        if(expiryPath.startsWith("$")){
            expiryPathDateTime=getValueFromResult(expiryPath,result);
        }else if(expiryPath.startsWith("#")){
            expiryPathDateTime=getValueFromParameters(expiryPath,args);
        }else if(expiryPath.equals("@now")){
            expiryPathDateTime= ZonedDateTime.now();
        }
        if (expiryPathDateTime instanceof ZonedDateTime time){
            Log.debug("current Time: {}",time);
            ZonedDateTime expiry= time.plusSeconds(expiryAdditionInSeconds);
            return Duration.between(ZonedDateTime.now(),expiry).get(ChronoUnit.SECONDS);
        }
        return null;
    }

    public static String getValueWithExcludePathRemoved(Object result, String[] excludePath, ObjectMapper objectMapper) throws JsonProcessingException {
        String toSave="";

        applyDynamicFilter(objectMapper,new HashSet<>(List.of(excludePath)));



        if(excludePath.length>0){

            JsonNode jsonNodeToSave=objectMapper.valueToTree(result);


            if(jsonNodeToSave.isArray()){

                for(JsonNode jsonNode:jsonNodeToSave){
                    removePathsFromJsonNode(jsonNode,excludePath);
                }
            }else{
                removePathsFromJsonNode(jsonNodeToSave,excludePath);

            }
            toSave=objectMapper.writeValueAsString(jsonNodeToSave);
        }else{
            toSave=objectMapper.writeValueAsString(result);
        }
        return toSave;

    }

    public static JsonNode removePathsFromJsonNode(JsonNode jsonNode,String[] excludePaths){
        for(String path:excludePaths){
            List<String> pathList= Arrays.asList(path.split("\\."));
            if(pathList.size()==1){
                ((ObjectNode) jsonNode).remove(path);
            }else if (pathList.size()>1){
                String pathToReplace=String.join("/",pathList.subList(0,pathList.size()-1));
                JsonNode jsonNodeToReplace=jsonNode.at(pathToReplace);
                ((ObjectNode) jsonNodeToReplace).remove(pathList.get(pathList.size()-1));

            }
        }
        return jsonNode;
    }

    public static void applyDynamicFilter(ObjectMapper objectMapper, Set<String> ignoredProps){

        FilterProvider filters=new SimpleFilterProvider().addFilter("dynamicFilter",new DynamicPropertyFilter(ignoredProps));

        objectMapper.setFilterProvider(filters);

        objectMapper.addMixIn(Object.class,DynamicFilterMixIn.class);
    }

    @JsonFilter("dynamicFilter")
    abstract class DynamicFilterMixIn{}

    public static Object getValueFromResult(String key,Object result) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (result==null){
            throw new IllegalArgumentException("The result is null when getting value from result");
        }

        key=key.split("\\$")[1];

        if(key.isEmpty()){
            return result.toString();
        }
        return PropertyUtils.getProperty(result,key);
    }

    public static Object getValueFromParameters(String key,Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(args.length>0 && key.startsWith("#")){
            String[] pathArr=key.split("#")[1].split(":",2);

            int idIndex=Integer.parseInt(pathArr[0]);

            String path=pathArr.length>1?pathArr[1]:"";

            if(idIndex>= args.length){
                throw new IllegalArgumentException("The id index is greater than the args length");
            }

            if (path.isEmpty()){
                return args[idIndex];
            }else{
                return PropertyUtils.getProperty(args[idIndex],path);
            }

        }
        return key;
    }

    public static Object getValueToReturn(String value,Class<?> actualClass,Class<?> returnType,ObjectMapper objectMapper) throws JsonProcessingException {
        if(value==null){
            return null;
        }

        Object toReturn;
        if(Collections.class.isAssignableFrom(returnType)){
            toReturn=objectMapper.readValue(value,objectMapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) returnType,actualClass));
        }else if(returnType== Optional.class){
            toReturn=objectMapper.readValue(value,actualClass);
            toReturn=Optional.of(toReturn);
        }else{
            toReturn=objectMapper.readValue(value,actualClass);
        }

        return toReturn;
    }

}
