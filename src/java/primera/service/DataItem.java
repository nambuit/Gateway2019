/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service;

import main.service.*;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Temitope
 */
@Getter
@Setter
public class DataItem {

    private String ItemHeader;

    private String[] ItemValues;

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder(ItemHeader);

        for (String val : ItemValues) {
            builder.append(" " + val);
        }
        return builder.toString();
    }

}





//DatePattern	Description
//'.' yyyy-MM	Roll over at the end of each month and at the beginning of the next month.
//'.' yyyy-MM-dd	Roll over at midnight each day. This is the default value.
//'.' yyyy-MM-dd-a	Roll over at midday and midnight of each day.
//'.' yyyy-MM-dd-HH	Roll over at the top of every hour.
//'.' yyyy-MM-dd-HH-mm	Roll over every minute.
//'.' yyyy-ww	Roll over on the first day of each week depending upon the locale.
//Following is a sample configuration file log4j.properties to generate log files rolling over at midday and midnight of each day.

//
//# Define the root logger with appender file
//log4j.rootLogger = DEBUG, FILE
//
//# Define the file appender
//log4j.appender.FILE=org.apache.log4j.DailyRollingFileAppender
//
//# Set the name of the file
//log4j.appender.FILE.File=${log}/log.out
//
//# Set the immediate flush to true (default)
//log4j.appender.FILE.ImmediateFlush=true
//
//# Set the threshold to debug mode
//log4j.appender.FILE.Threshold=debug
//
//# Set the append to false, should not overwrite
//log4j.appender.FILE.Append=true
//
//# Set the DatePattern
//log4j.appender.FILE.DatePattern='.' yyyy-MM-dd-a
//
//# Define the layout for file appender
//log4j.appender.FILE.layout=org.apache.log4j.PatternLayout
//log4j.appender.FILE.layout.conversionPattern=%m%n