


//MyServlet.java
import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.MyServlet.MyServletException;
import javax.MyServlet.http.HttpMyServlet;
import javax.MyServlet.http.HttpMyServletRequest;
import javax.MyServlet.http.HttpMyServletResponse;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyServlet extends HttpMyServlet {

    protected void processRequest(HttpMyServletRequest request, HttpMyServletResponse response)
            throws MyServletException, IOException, SQLException {

        try {
            //READ FROM INPUT STREAM
            response.setContentType("text/plain");
            PrintWriter out1 = response.getWriter();
            out1.println("MyServlet Invoked");
            BufferedReader br = request.getReader();
            String buf = br.readLine();
            //out1.println(buf);
            //DATAGROUPS
              int NGi=2;
              int locks=0;//AS we are using only one maptable
             
            
            //CONNECT TO DATABASE
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/final project", "root", "jyotsna");
            System.out.println("Connected database successfully...");
            Statement stmt = con.createStatement();

            int x = 1001;
            int y;
            String ClientID = null;
            //READ TIMESTAMPS
            long[] l = new long[2];
            int i = 0;
            for (String retval : buf.split(",")) {
                if (i == 2) {
                    ClientID = retval;
                    break;
                }
               // out1.println(retval);
                l[i] = Long.parseLong(retval);
                i++;
            }
            //GET CLIENT ID
            int cID = Integer.parseInt(ClientID);
            out1.println("client id " + cID);
            //LUID
            y = cID * 100 + 1;

            //CLACULATE TIMESTAMPS
            long ST2 = new java.util.Date().getTime();
            long ST1 = ST2 - l[0] + l[1];
           
            
            
            //GENERATE SUBSTRING REMOVING THE TIMESTAMPS
            int i1 = buf.lastIndexOf("/");
            buf = buf.substring(i1 + 1);
            //out1.println(buf);

            //PARSE DATA AND SEND TO DATABASE
            int y1 = y;
            //CHECK IF DATA EXISTS IN MAPTABLE
            String isEmpty = " select count(*) from maptable1";
            ResultSet rs;
            rs = stmt.executeQuery(isEmpty);
            rs.next();
            out1.println(rs.getBoolean(1));
            //IF DATA DOES EXISTS ......REPLICATE DATA INTO SERVER DATABASE
            if (rs.getBoolean(1) == false) {
                locks=1;int x1=NGi/2;
               if(locks<x1){System.exit(0);}
                String Serverquery = " insert into server_database (GUID,data)" + " values (?, ?)";
                String map1query = " insert into maptable1 (GUID,LUID,TIMESTAMP)" + " values (?, ?, ?)";
                PreparedStatement preparedStmt = con.prepareStatement(Serverquery);
                PreparedStatement pstmt_map1 = con.prepareStatement(map1query);
                for (String retval : buf.split(",")) {
                    out1.println(retval);
                    pstmt_map1.setInt(1, x);
                    pstmt_map1.setInt(2, y1);
                    pstmt_map1.setLong(3, ST1);

                    y1++;
                    pstmt_map1.execute();
                    preparedStmt.setInt(1, x);
                    preparedStmt.setString(2, retval);
                    preparedStmt.execute();

                    x = x + 1;

                }
              
            } //IF DATA ALREADY EXISTS .....COMPARE WITH EXISTING TIMESTAMP
            else {
                x = 1001;
                y1 = y;
                long l2;
                String ts = "select TIMESTAMP from maptable1";
                ResultSet r;
                r = stmt.executeQuery(ts);
                r.next();
               l2 = r.getLong(1);
                out1.println("timestamp of previous update" + l2 + " and present update" + ST1);
                //IF PRESENT TIMESTAMP IS GREATER UPDATE IT
                if (l2 < ST1) {
                    out1.println("MORE than timestamp in database");
                    String TS = "delete from maptable1";

                    String Ts = "delete from server_database";
                    stmt.executeUpdate(TS);
                    stmt.executeUpdate(Ts);

                    String Serverquery = " insert into server_database (GUID,data)" + " values (?, ?)";
                    String map1query = " insert into maptable1 (GUID,LUID,TIMESTAMP)" + " values (?, ?, ?)";
                   

                    PreparedStatement preparedStmt = con.prepareStatement(Serverquery);
                    PreparedStatement pstmt_map1 = con.prepareStatement(map1query);
                    
                    int k = 0;
                    for (String retval : buf.split(",")) {
                                                 
                            pstmt_map1.setInt(1, x);
                            pstmt_map1.setInt(2, y);
                            pstmt_map1.setLong(3, ST1);
                            pstmt_map1.execute();

                      

                        y++;
                        preparedStmt.setInt(1, x);
                        preparedStmt.setString(2, retval);
                        preparedStmt.execute();

                        x = x + 1;
                        k++;

                    }

                }
            }
                out1.println("<READ FROM DATABASE>");

                ResultSet rs1, rs2 = null;
                int[] a;
                int k = 0;
                    //READ EACH CLIENTS DATA 

                String displayStr = " select data from  server_database";
                rs1 = stmt.executeQuery(displayStr);
                while (rs1.next()) {

                    out1.println(rs1.getString(1) + ",");

                }

            locks--;

        } catch (Exception e) { System.out.println("Cant be updated");
        }
    }

    protected void doGet(HttpMyServletRequest req, HttpMyServletResponse res)
            throws MyServletException, IOException {
        try {
            processRequest(req, res);
        } catch (SQLException ex) {
            Logger.getLogger(MyServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected void doPost(HttpMyServletRequest req, HttpMyServletResponse res)
            throws MyServletException, IOException {
        try {
            processRequest(req, res);
        } catch (SQLException ex) {
            Logger.getLogger(MyServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}












