package com.driver;


import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, List<String>> orderPartnerMap;

    private Set<String> orderNotAssigned;

    public OrderRepository() {
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.orderPartnerMap = new HashMap<>();
        this.orderNotAssigned = new HashSet<>();
    }

    public void addOrder(Order order){
        orderMap.put(order.getId(), order);
        orderNotAssigned.add(order.getId());
    }

    public void addPartner(String partnerId){
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        partnerMap.get(partnerId).setNumberOfOrders(partnerMap.get(partnerId).getNumberOfOrders()+1);
        if(orderPartnerMap.containsKey(partnerId)){
            List<String> orderList = orderPartnerMap.get(partnerId);
            orderList.add(orderId);
            orderNotAssigned.remove(orderId);
            return;
        }
        orderPartnerMap.put(partnerId, new ArrayList<>(Arrays.asList(orderId)));
        orderNotAssigned.remove(orderId);
    }

    public Order getOrderById(String orderId){
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return orderPartnerMap.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        List<String> orderList = new ArrayList<>();

        List<String> orderIdList = orderPartnerMap.get(partnerId);
        for(String order : orderIdList){
            orderList.add(orderMap.get(order).getId());
        }
        return orderList;
    }

    public List<String> getAllOrders(){
        Collection<Order> values = orderMap.values();

        List<String> orderList = new ArrayList<>();
        for(Order o : values){
            orderList.add(o.getId());
        }
        return orderList;
    }

    public int getCountOfUnassignedOrders(){
        return orderNotAssigned.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int numericalTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3,5));
        int count = 0;
        for(String orderId : orderPartnerMap.get(partnerId)){
            if(orderMap.get(orderId).getDeliveryTime()>numericalTime){
                count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;
        if(orderPartnerMap.containsKey(partnerId)){
            for(String currentOrderId : orderPartnerMap.get(partnerId)){
                if(orderMap.get(currentOrderId).getDeliveryTime()>latestTime){
                    latestTime = orderMap.get(currentOrderId).getDeliveryTime();
                }
            }
        }
        int hours = latestTime/60;
        int minute = latestTime%60;

        String strHours = Integer.toString(hours);
        if(strHours.length() == 1){
            strHours = "0"+strHours;
        }

        String minutes = Integer.toString(minute);
        if(minutes.length() == 1){
            minutes = "0"+minutes;
        }
        return strHours + ":" + minutes;
    }

    public void deletePartnerById(String partnerId){
        if(!orderPartnerMap.isEmpty()){
            orderNotAssigned.addAll(orderPartnerMap.get(partnerId));
        }
        orderPartnerMap.remove(partnerId);
        partnerMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId){
        orderMap.remove(orderId);
        if(orderNotAssigned.contains(orderId)){
            orderNotAssigned.remove(orderId);
        }
        else {
            for(List<String> listOfOrderIds : orderPartnerMap.values()){
                listOfOrderIds.remove(orderId);
            }
        }
    }

}
