/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Temitope
 */
@Getter
@Setter
public class ofsParam {

    private String operation;

    private String version;

    private String[] Options;

    private String transaction_id;

    private String[] credentials;

    private List<DataItem> DataItems;
}
