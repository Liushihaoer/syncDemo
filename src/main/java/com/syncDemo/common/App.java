package com.syncDemo.common;

import com.syncDemo.gpcsFeedBack.GpcsFeedBack;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
//        SyncGoods synGoods = new SyncGoods();
        GpcsFeedBack gpcsFeedBack = new GpcsFeedBack();
        try {
            gpcsFeedBack.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
