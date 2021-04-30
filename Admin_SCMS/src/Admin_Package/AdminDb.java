/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Admin_Package;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author kisho
 */
public class AdminDb {
    Connection con = null;
    Statement st = null;
    CallableStatement cst= null;
    
    public void init(){
        try {
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","orcl");
            st = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DefaultTableModel getProducts(){
        DefaultTableModel model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        model.addColumn("ProductId");
        model.addColumn("ProductName");
        model.addColumn("Price");
        model.addColumn("Sales");
        
        ResultSet rs = null;
        try {
            rs = st.executeQuery("select * from (select * from products) t1 left join (select product_Id, sum(quantity) as sales from Purchases group by product_Id) t2 on t1.product_Id = t2.product_Id");
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
            Logger.getLogger(AdminDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;
    }
    
    public int deleteProduct(int id){
        try{
            st.executeUpdate("delete Products where product_id = " + id);
            return 1;
        }catch(SQLException ex){
            Logger.getLogger(AdminDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    public int updateProduct(int id, String pname, int price){
        int status = -1;
        try{
            status = st.executeUpdate("update Products set price = "+price+" , pname = \'"+pname+"\' where product_id = " + id);
            return (status>0?1:-1);
        }catch(SQLException ex){
            Logger.getLogger(AdminDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public int insertProduct(String pname, int price){
        try {
            cst = con.prepareCall("{call add_Product(?,?,?)}");
            cst.setString(1, pname);
            cst.setInt(2, price);
            cst.registerOutParameter(3, java.sql.Types.NUMERIC);
            cst.execute();
            return cst.getInt(3);
        } catch (SQLException ex) {
            Logger.getLogger(AdminDb.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
}
