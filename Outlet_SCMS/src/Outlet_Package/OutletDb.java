/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outlet_Package;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.*;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
/**
 *
 * @author kisho
 */
public class OutletDb {
    Connection con = null;
    Statement st;
    CallableStatement cst;
    
    public void init(){
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","orcl");
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int loginOutlet(int id, String pwd){
        try {
            PreparedStatement pst = con.prepareStatement("select * from outlets where outlet_id = ? and password = ?");
            pst.setInt(1, id);
            pst.setString(2, pwd);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return 1;
            }else{
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    public DefaultTableModel getMenu(int id){
        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        model.addColumn("Product Id");
        model.addColumn("Product Name");
        model.addColumn("Price");
        model.addColumn("Sales Qty.");
        
        ResultSet rs = null;
        try {
            st = con.createStatement();
            st.execute("create or replace view v1 as (select product_id,sum(quantity) qty_sum from purchases where bill_id in (select bill_id from bills where outlet_id = "+id+") group by product_id )");
            rs = st.executeQuery("select product_id, pname, price, qty_sum from (select outlet_menus.product_id, qty_sum from outlet_menus left join v1 on outlet_menus.product_id = v1.product_id) natural join (select * from products)");
            
            int pid, sales;
            String pname;
            float price;
            while(rs.next()){
                pid = rs.getInt(1);
                pname = rs.getString(2);
                price = rs.getFloat(3);
                sales = rs.getInt(4);
                model.addRow(new Object[] {pid, pname, price, sales});
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;
    }

    public void getActionMenu(DefaultComboBoxModel model1, DefaultComboBoxModel model2, int id){
        try {
            ResultSet rs = st.executeQuery("select * from (select * from Outlet_Menus where outlet_id = "+id+") natural join (select * from Products)");
            while(rs.next()){
                String pid = rs.getString(1);
                String productName = rs.getString(3);
                String price = rs.getString(4);
                model2.addElement(pid + " : " + productName+" : "+ price);
            }
            rs = st.executeQuery("select * from products where product_id not in (select product_id from outlet_menus where outlet_id = "+id+")");
            while(rs.next()){
                String pid = rs.getString(1);
                String productName = rs.getString(2);
                String price = rs.getString(3);
                model1.addElement(pid+ " : " + productName + " : "+ price);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int deleteProduct(int oid, int pid){
        try {
            int s = st.executeUpdate("delete outlet_menus where outlet_id = "+oid+" and product_id = "+pid);
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }  
    }
    
    public int addProduct(int oid, int pid){
        try {
            st.executeUpdate("insert into outlet_menus values("+oid+", "+pid+")");
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }  
    }
    
    public int[] addBill(int o_id,int c_id, String[] prods, String[] qty){
            int res[] = new int[2];
            res[0] = 0;
            res[1] = -1;
        try {
            cst = con.prepareCall("{call gen_bill(?,?,?,?,?,?)}");
            cst.setInt(1, o_id);
            cst.setInt(2, c_id);
            ArrayDescriptor des = ArrayDescriptor.createDescriptor("NUMBER_ARRAY", con);
            ARRAY prodArray = new ARRAY(des, con, prods);
            cst.setArray(3, prodArray);
            Array qtyArray = new ARRAY(des, con, qty);
            cst.setArray(4, qtyArray);
            cst.registerOutParameter(5, java.sql.Types.NUMERIC);
            cst.registerOutParameter(6, java.sql.Types.NUMERIC);
            cst.execute();
            
            res[0]= cst.getInt(5);
            res[1] = cst.getInt(6);
            
            return res;   
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
            return res;
        }
    }

    public int addCustomer(String name, int phone){
        try {
            cst = con.prepareCall("{call add_customer(?,?,?)}");
            cst.setString(1, name);
            cst.setInt(2, phone);
            cst.registerOutParameter(3, java.sql.Types.NUMERIC);
            cst.execute();
            int status = cst.getInt(3);
            return status;
        } catch (SQLException ex) {
            Logger.getLogger(OutletDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
}
