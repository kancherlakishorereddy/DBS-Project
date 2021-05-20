/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Customer_Package;

import java.sql.*;
import java.util.logging.*;

/**
 *
 * @author kisho
 */
public class CustomerDb {
    Connection con = null;
    Statement st;
    CallableStatement cst;
    private String password = null;
    private int phone_num = 0;
    private String cname = null;
    
    public void init(){
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","orcl");
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getpwd(){
        return password;
    }
    
    public int findCustomerByPhone(int phone){
        try {
            phone_num = phone;
            cst = con.prepareCall("{call match_username(?,?,?)}");
            cst.setInt(1, phone);
            cst.registerOutParameter(2, java.sql.Types.VARCHAR);
            cst.registerOutParameter(3, java.sql.Types.NUMERIC);
            cst.execute();
            password = cst.getString(2);
            return cst.getInt(3);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
      
    public int loginExistingUser(String pwd){
        if(pwd.equals(password)){
            return loginUser();
        }else{
            return 2;
        }
    }
    
    public int loginNewUser(String pwd){
        try {
            cst = con.prepareCall("{call update_password(?,?,?)}");
            cst.setInt(1, phone_num);
            cst.setString(2, pwd);
            cst.registerOutParameter(3, java.sql.Types.NUMERIC);
            cst.execute();
            
            if(cst.getInt(3)==1){
                loginUser();
            }else{
                return -1;
            }
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private int loginUser(){
        try {
            cst = con.prepareCall("{call login_user(?,?)}");
            cst.setInt(1, phone_num);
            cst.registerOutParameter(2, java.sql.Types.NUMERIC);
            cst.execute();
            
            return cst.getInt(2);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    public void logoutUser(){
        try {
            cst = con.prepareCall("{call logout_user(?,?)}");
            cst.setInt(1,phone_num);
            cst.registerOutParameter(2, java.sql.Types.NUMERIC);
            cst.execute();
            password = null;
            phone_num = -1;
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
