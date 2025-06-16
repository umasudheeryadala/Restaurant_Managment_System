package com.tastes_of_india.restaurantManagement.service.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;

public class PaginationUtil {

    private static final String HEADER_X_TOTAL_COUNT="X-Total-Count";

    public static <T> HttpHeaders generatePaginationHttpHeaders(UriComponentsBuilder uriComponentsBuilder, Page<T> page){
        HttpHeaders headers=new HttpHeaders();
        headers.set(HEADER_X_TOTAL_COUNT,Long.toString(page.getTotalElements()));
        headers.set("Link",prepareLinkHeaders(uriComponentsBuilder,page));
        return headers;
    }

    public static String prepareLinkHeaders(UriComponentsBuilder uriComponentsBuilder,Page<?> page){
        int pageNumber= page.getNumber();
        int pageSize=page.getSize();
        StringBuilder link=new StringBuilder();
        if(pageNumber< page.getTotalPages()-1){
            link.append(prepareLink(uriComponentsBuilder,pageNumber+1,pageSize,"next")).append(",");
        }

        if(pageNumber>0){
            link.append(prepareLink(uriComponentsBuilder,pageNumber-1,pageSize,"prev")).append(",");
        }

        link.append(prepareLink(uriComponentsBuilder,page.getTotalPages()-1,pageSize,"last")).append(",").append(prepareLink(uriComponentsBuilder,0,pageSize,"first"));
        return link.toString();
    }

    private static String prepareLink(UriComponentsBuilder uriComponentsBuilder,int pageNumber,int pageSize,String relType){
        return MessageFormat.format("<{0};re=\"{1}\"",preparePageUrl(uriComponentsBuilder,pageNumber,pageSize),relType);
    }

    private static String preparePageUrl(UriComponentsBuilder uriComponentsBuilder,int pageNumber,int pageSize){
        return uriComponentsBuilder.replaceQueryParam("page",new Object[]{Integer.toString(pageNumber)}).replaceQueryParam("size",new Object[]{Integer.toString(pageSize)}).toUriString().replace(",","%2C").replace(";","%3B");
    }
}
