package com.facilio.simulator;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class Random {


    public static void main(String[] args) {
        List<Map<String, Object>> values = new ArrayList<>();
        // water
        addValues(values, "pH", 6.5, 8, 1,0.25);   // 7-8
        addValues(values, "ORP", 350, 500, 2,15);    //
        addValues(values, "Chlorine", 0.4, 2.2, 1,0.25); 
        addValues(values, "Battery Level", 4.1, 5, 1,0.1);
      //  addValues(values, "Calcium", 125, 450, 2,15);    //
      //  addValues(values, "Copper", 0.1, 0.5, 1,0.25);    //


        // air
//        addValues(values, "CO2", 600, 1000, 2, 15);//
//        addValues(values, "Temperature", 18, 29, 1, 0.5);//
//        addValues(values, "Humidity", 25, 65, 2, 5);//
//        addValues(values, "PM2.5", 31, 39, 1, 0.5);//
//        addValues(values, "VOC", 200, 400, 1, 12.5);//
//        addValues(values, "CO", 1, 18, 1, 0.2);
//        addValues(values, "Formaldehyde", 0, 1.8, 1, 0.1);
//        addValues(values, "H2S", 0, 2, 1, 0.1);

        generateTimeStamps(values);
    }

    private static void addValues(List<Map<String, Object>> values, String name, Number min, Number max, int type, Number maxIntervalChange) {
        Map<String, Object> map = new HashMap<>();
        map.put("min", min);
        map.put("max", max);
        map.put("type", type);
        map.put("name", name);
        map.put("maxInterval", maxIntervalChange);
        values.add(map);
    }

    private static double nextDouble (ThreadLocalRandom t, double min, double max) {
        return t.nextDouble(min, max);
    }

    private static int nextInt (ThreadLocalRandom t, int min, int max) {
        return t.nextInt(min, max);
    }

    private static void generateTimeStamps(List<Map<String, Object>> values) {
        long startTime = 1567036800000l;
        long endTime = 1571097600000l;
        long interval = 45;
        interval = interval * 60 * 1000;

        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        StringBuilder sb = new StringBuilder();
        sb.append("Time\t");
        for (Map map: values) {
            sb.append(map.get("name") + "\t");
        }
        System.out.println(sb.toString());
        sb.setLength(0);

        Map<String, Number> lastValues = new HashMap<>();

        while ((startTime + interval) < endTime) {
            sb.append(startTime);
            sb.append("\t");
            for (Map map: values) {
                int type = ((Number) map.get("type")).intValue();
                String name = (String) map.get("name");
                Number maxInterval = (Number) map.get("maxInterval");
                switch (type) {
                    case 1: {
                        Number lastValue = lastValues.get(name);
                        double v;
                        if (lastValue != null && maxInterval != null) {
                            double d = nextDouble(localRandom, -1 * maxInterval.doubleValue(), maxInterval.doubleValue());
                            v = lastValue.doubleValue() + d;
                            if (v < ((Number) map.get("min")).doubleValue()) {
                                v = ((Number) map.get("min")).doubleValue();
                            }
                            if (v > ((Number) map.get("max")).doubleValue()) {
                                v = ((Number) map.get("max")).doubleValue();
                            }
                        }
                        else {
                            v = nextDouble(localRandom, ((Number) map.get("min")).doubleValue(), ((Number) map.get("max")).doubleValue());
                        }
                        sb.append(String.format("%.2f", v));
                        lastValues.put(name, v);
                        break;
                    }
                    case 2: {
                    	int i;
                    	 Number lastValue = lastValues.get(name);
	                     if (lastValue != null && maxInterval != null) {
	                         int j = nextInt(localRandom, -1 * maxInterval.intValue(), maxInterval.intValue());
	                         i = lastValue.intValue() + j;
	                         if (i < ((Number) map.get("min")).intValue()) {
	                             i = ((Number) map.get("min")).intValue();
	                         }
	                         if (i > ((Number) map.get("max")).intValue()) {
	                             i = ((Number) map.get("max")).intValue();
	                         }
	                     }
                    	
                    	else {
                    		   i = nextInt(localRandom, ((Number) map.get("min")).intValue(), ((Number) map.get("max")).intValue());
                    	}
                        sb.append(i);    		
                        lastValues.put(name, i);
                        break;
                    }
                }
                sb.append("\t");
            }
            System.out.println(sb.toString());
            startTime += interval;
            sb.setLength(0);
        }
    }
}
