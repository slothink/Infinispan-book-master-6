package com.packtpub.infinispan.chapter4;

import model.Ticket;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import utils.IOUtils;

import javax.transaction.*;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by slothink on 2015-04-13.
 */
public class SampleTcpCache {
    DefaultCacheManager m = null;
    Cache<String, Ticket> cache = null;

    public void start() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {

        try {
            m = new DefaultCacheManager("sample-tcp.xml");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        m.addListener(new SampleListener());

        cache = m.getCache("clusteredCache");
        cache.addListener(new SampleListener());


        String command = ""; // Line read from standard in
        int ticketid = 1;

        IOUtils.dumpWelcomeMessage();

        while (true){
            command = IOUtils.readLine("> ");
            if (command.equals("book")) {

                String name = IOUtils.readLine("Enter name ");
                String show = IOUtils.readLine("Enter show ");

                Ticket ticket = new Ticket(name,show);
                cache.put(name, ticket);
                log("Booked ticket "+ticket);
                ticketid++;
            }
            else if (command.equals("pay")) {
                String name = IOUtils.readLine("Enter ticketid ");
                Ticket ticket = cache.remove(name);
                log("Checked out ticket "+ticket);
            }
            else if (command.equals("list")) {
                Set<String> set = cache.keySet();
                for (String ticket: set) {
                    System.out.println(cache.get(ticket));
                }
            }
            else if (command.equals("quit")) {
                cache.stop();
                log("Bye");
                break;
            }
            else if (command.equals("change")) {
                String id = IOUtils.readLine("Enter Ticketid ");
                String show = IOUtils.readLine("Enter show ");
                Ticket ticket = cache.get(Integer.parseInt(id));
                ticket.setShow(show);
                beginTransaction();
                cache.put(id, ticket);
                String captcha =
                        UUID.randomUUID().toString().substring(0,4);
                String check =
                        IOUtils.readLine("Enter captcha "+captcha);
                if (captcha.equals(check)) {
                    commitTransaction();
                    log("Updated ticket "+ticket);
                }
                else {
                    rollbackTransaction();
                    log("Updated failed!");

                }
            }

            else {
                log("Unknown command "+command);
                IOUtils.dumpWelcomeMessage();
            }
        }




    }

    public static void main(String[] args) throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        new SampleTcpCache().start();

    }
    public static void log(String s){
        System.out.println(s);
    }
    public void beginTransaction() throws NotSupportedException, SystemException {
        cache.getAdvancedCache().
                getTransactionManager().begin();
    }
    public void commitTransaction() throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        cache.getAdvancedCache().
                getTransactionManager().commit();
    }
    public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
        cache.getAdvancedCache().
                getTransactionManager().rollback();
    }
}
