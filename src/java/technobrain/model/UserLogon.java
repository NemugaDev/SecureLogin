/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package technobrain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author morgan
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLogon {
    private String email;
    private String name;
    private int id;
    private String password;
}
