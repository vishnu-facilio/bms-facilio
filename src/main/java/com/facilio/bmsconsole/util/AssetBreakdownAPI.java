package com.facilio.bmsconsole.util;


public class AssetBreakdownAPI {
       
       public static long calculateDurationInSeconds(Long fromTime,Long toTime){
               return (toTime-fromTime) / 1000;
       }
       
}
