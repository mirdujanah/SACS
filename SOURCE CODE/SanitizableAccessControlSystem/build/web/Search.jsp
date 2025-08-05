<%-- 
    Document   : Search
    Created on : Jan 27, 2023, 2:58:09 PM
    Author     : Murthi
--%>


<%@page import="SACS.SQLconnection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String kword = request.getParameter("kword");
    Connection con = SQLconnection.getconnection();
    Statement st = con.createStatement();
    try {

        ResultSet rs = st.executeQuery("select * from uploads where kword LIKE '%" + kword + "%' ");
        if (rs.next()) {
          response.sendRedirect("searchedFiles.jsp?sword="+kword);
        } else {
            response.sendRedirect("SearchFiles.jsp?Not");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
%>
