package com.dinhcv.ticketmanagement.model;

import com.dinhcv.ticketmanagement.model.database.entities.m_ticket;
import com.dinhcv.ticketmanagement.model.structure.StatisticInfo;
import com.dinhcv.ticketmanagement.model.structure.TicketInfo;
import com.dinhcv.ticketmanagement.utils.Debug;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by dinhcv on 02/04/2017.
 */

/*
 Status: 1 - trong bai
 Status: 2 - ra khoi bai

 Sex: 1 - nam
 Sex: 2- nu

 Ticket type: 1 - carin
 Ticket type: 2 - carout

 */

public class TicketModel {

    private TicketInfo entity2TicketInfo(m_ticket e) {

        TicketInfo p = new TicketInfo();

        p.setId(e.id);
        p.setUserId( e.user_id);
        p.setTicketType( e.ticket_type);
        p.setLisencePlate(e.lisence_plate);
        p.setLisenceCode(e.lisence_code);
        p.setCarInImagePath( e.car_in_image_path);
        p.setCarOutImagePath( e.car_out_image_path);
        p.setStatus(e.status);
        p.setTimeIn( new Date(e.time_in));
        p.setTimeOut( new Date(e.time_out));
        p.setFee(e.fee);
        p.setTemp(e.temp);

        return p;
    }


    public List<TicketInfo> getFoodList(){
        List<m_ticket> tickets = m_ticket.find(
                m_ticket.class,
                null,
                null,
                null,
                null,
                null
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return null;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d ticket of List", tickets.size());
        }

        //convert to Info

        List<TicketInfo> ticketInfos = new ArrayList<>();
        for (int i = 0; i<tickets.size(); i++) {
            TicketInfo ticketInfo = entity2TicketInfo(tickets.get(i));
            // add to list
            ticketInfos.add(ticketInfo);
        }

        return ticketInfos;
    }


    public List<TicketInfo> searchTicket(String lisencePlate, boolean isTime, Date dateFrom, Date dateTo, int status,
                                         int pageIndex, int pageSize){
        String condition = "";
        if ((lisencePlate == null) || (lisencePlate.isEmpty())){
            if (isTime){
                if (status == 0) {
                    condition = "time_in > " + dateFrom.getTime() + " AND time_in < "+ dateTo.getTime() + " AND status <> "+status;
                }else {
                    condition = "time_in > " + dateFrom.getTime() + " AND time_in < "+ dateTo.getTime() + " AND status = "+status;
                }
            }else {
                if (status == 0) {
                    condition = "status <> " + status;
                }else {
                    condition = "status != " + status;
                }
            }
        }else {
            if (isTime){
                if (status == 0) {
                    condition = "lisence_plate like '%"+ lisencePlate + "%' AND time_in < " + dateTo.getTime() + " AND time_out > "+
                            dateFrom.getTime() + " AND status <> "+status;
                }else {
                    condition = "lisence_plate like '%"+ lisencePlate + "%' AND time_in < " + dateTo.getTime() + " AND time_out > "+
                            dateFrom.getTime() + " AND status = "+status;
                }
            }else {
                if (status == 0) {
                    condition = "lisence_plate like '%"+ lisencePlate + "%' AND status <> "+status;
                }else {
                    condition = "lisence_plate like '%"+ lisencePlate + "%' AND status = "+status;
                }
            }
        }

        Debug.normal("Where condition: "+condition);
        //limit sql
        final String limitSql = String.format(Locale.US, "%d, %d", (pageSize * pageIndex), pageSize);

        List<m_ticket>  tickets = m_ticket.find(
                m_ticket.class,
                condition,
                null,
                null,
                null,
                limitSql
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return null;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d food of List", tickets.size());
        }

        //convert to InspectorInfo

        List<TicketInfo> ticketInfos = new ArrayList<>();
        for (int i = 0; i<tickets.size(); i++) {
            TicketInfo ticketInfo = entity2TicketInfo(tickets.get(i));
            // add to list
            ticketInfos.add(ticketInfo);
        }

        return ticketInfos;
    }


    public List<StatisticInfo> searchStatisticData(Date dateFrom, Date dateTo){
        String condition = "time_out > " + dateFrom.getTime() + " AND time_out < "+ dateTo.getTime();


        Debug.normal("Where condition: "+condition);
        //limit sql

        List<m_ticket>  tickets = m_ticket.find(
                m_ticket.class,
                condition,
                null,
                null,
                null,
                null
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return null;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d food of List", tickets.size());
        }

        //convert to info
        long oneDay = 24 *60 *60 *1000;
        List<StatisticInfo> statisticInfos = new ArrayList<>();

        for (int i = 0; i<tickets.size(); i++) {
            m_ticket ticket = tickets.get(i);
            if (ticket.temp == 0) {
                // add to list
                int carin = 0;
                int carout = 0;
                long revenue = 0;
                if (ticket.status == 1) {
                    carin = 1;
                } else {
                    carout = 1;
                }

                revenue = ticket.fee;


                for (int j = i + 1; j < tickets.size(); j++) {
                    m_ticket ticket1 = tickets.get(j);
                    if (ticket1.temp == 0) {

                        if (Math.abs(ticket.time_out - ticket1.time_in) < oneDay){
                            carin++;
                        }

                        if (Math.abs(ticket.time_out - ticket1.time_out) < oneDay) {
                            if (ticket1.status == 2) {
                                carout++;
                            }
                            // revenue
                            revenue += tickets.get(j).fee;

                            // update
                            tickets.get(j).temp = 1;
                        }
                    }
                }

                StatisticInfo statisticInfo = new StatisticInfo();
                statisticInfo.setCountIn(carin);
                statisticInfo.setCountOut(carout);
                statisticInfo.setDate(new Date(ticket.time_out));
                statisticInfo.setRevenue(revenue);

                statisticInfos.add(statisticInfo);
            }
        }

        return statisticInfos;
    }


    public long getRevenueStatisticData(Date dateFrom, Date dateTo){
        String condition = "time_out > " + dateFrom.getTime() + " AND time_out < "+ dateTo.getTime();


        Debug.normal("Where condition: "+condition);
        //limit sql

        List<m_ticket>  tickets = m_ticket.find(
                m_ticket.class,
                condition,
                null,
                null,
                null,
                null
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return 0;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d food of List", tickets.size());
        }

        long revenue = 0;
        for (int i = 0; i<tickets.size(); i++) {
            revenue += tickets.get(i).fee;
        }

        return revenue;
    }


    public long getRevenueToday(Date today){
        // set end date
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date endDay = cal.getTime();
        String condition = "time_out > " + today.getTime() + " AND time_out < "+ endDay.getTime();


        Debug.normal("Where condition: "+condition);
        //limit sql

        List<m_ticket>  tickets = m_ticket.find(
                m_ticket.class,
                condition,
                null,
                null,
                null,
                null
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return 0;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d food of List", tickets.size());
        }

        List<StatisticInfo> statisticInfos = new ArrayList<>();
        long revenue = 0;
        for (int i = 0; i<tickets.size(); i++) {
            m_ticket ticket = tickets.get(i);
            revenue += ticket.fee;
        }

        return revenue;
    }


    public int getTotalCarInPark() {
        int status = 1;
        String whereStr = "status = " + status;
        return m_ticket.count(m_ticket.class, whereStr);
    }

    public int getTotalCarInOnDay(Date today) {
        int status = 1;
        String whereStr = " time_in > "+today.getTime();
        return m_ticket.count(m_ticket.class, whereStr);
    }

    public int getTotalCarOutOnDay(Date today) {
        int status = 2;
        String whereStr = "status = " + status + " AND time_out > "+today.getTime();
        return m_ticket.count(m_ticket.class, whereStr);
    }

    public List<TicketInfo> getTicketInParkingList(int pageIndex, int pageSize){
        String condition = "status = ?";
        //limit sql
        final String limitSql = String.format(Locale.US, "%d, %d", (pageSize * pageIndex), pageSize);

        List<m_ticket> tickets = m_ticket.find(
                m_ticket.class,
                condition,
                new String[]{String.valueOf(1)},
                null,
                "id",
                limitSql
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return null;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d Ticket of List", tickets.size());
        }

        //convert to Info

        List<TicketInfo> ticketInfos = new ArrayList<>();
        for (int i = 0; i<tickets.size(); i++) {
            TicketInfo ticketInfo = entity2TicketInfo(tickets.get(i));
            // add to list
            ticketInfos.add(ticketInfo);
        }

        return ticketInfos;
    }

    public TicketInfo getTicketByID(int id){

        m_ticket ticket = m_ticket.findById(
                m_ticket.class,
                id, "id"
        );

        //check results
        if (ticket == null) {
            Debug.normal("Ticket is null");
            return null;
        }

        //convert to food cate info

        TicketInfo ticketInfo  = entity2TicketInfo(ticket);
        Debug.normal("Ticket type: "+ticketInfo.getTicketType() +" And id = "+ticketInfo.getId());

        return ticketInfo;
    }


    public TicketInfo getTicketByLisencePlateInParking(String lisencePlate){

        String condition = "lisence_code = ? AND status = 1";

        List<m_ticket> tickets = m_ticket.find(
                m_ticket.class,
                condition,
                new String[] {lisencePlate},
                null,
                null,
                null
        );

        //check results
        if ((tickets == null) || (tickets.isEmpty())) {
            Debug.normal("Ticket is null");
            return null;
        }

        if (tickets.size() >1 ){
            Debug.warn("Found %d Ticket of List", tickets.size());
        }

        //convert to Info

        TicketInfo ticketInfo = entity2TicketInfo(tickets.get(0));

        return ticketInfo;
    }


    public boolean saveTicket(TicketInfo ticketInfo){
        m_ticket ticket = new m_ticket();
        ticket.id = ticketInfo.getId();
        ticket.user_id = ticketInfo.getUserId();
        ticket.ticket_type = ticketInfo.getTicketType();
        String lisence = ticketInfo.getLisencePlate();
        ticket.lisence_plate = lisence.toUpperCase();
        ticket.lisence_code = ticketInfo.getLisenceCode();
        ticket.status   = ticketInfo.getStatus();
        ticket.car_in_image_path   = ticketInfo.getCarInImagePath();
        ticket.car_out_image_path   = ticketInfo.getCarOutImagePath();
        ticket.time_in   = ticketInfo.getTimeIn().getTime();
        ticket.time_out   = ticketInfo.getTimeOut().getTime();
        ticket.fee   = ticketInfo.getFee();
        ticket.temp   = ticketInfo.getTemp();

        long t = ticket.save();
        if (t < 0){
            Debug.error("Error. Can not save account");
            return false;
        }
        Debug.normal("Success. Save account success");
        return true;
    }

}
