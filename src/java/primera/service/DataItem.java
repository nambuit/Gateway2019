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
