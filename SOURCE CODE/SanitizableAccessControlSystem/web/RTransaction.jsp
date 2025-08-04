<%-- 
    Document   : RTransaction
    Created on : Jan 27, 2023, 7:25:03 PM
    Author     : Murthi
--%>
<%@page import="SACS.SQLconnection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@100;200;300;400;500;600;700;800;900&display=swap" rel="stylesheet">

        <title>Sanitizable Access Control System</title>

        <!-- Bootstrap core CSS -->
        <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">


        <!-- Additional CSS Files -->
        <link rel="stylesheet" href="assets/css/fontawesome.css">
        <link rel="stylesheet" href="assets/css/templatemo-574-mexant.css">
        <link rel="stylesheet" href="assets/css/owl.css">
        <link rel="stylesheet" href="assets/css/animate.css">
        <link rel="stylesheet" href="https://unpkg.com/swiper@7/swiper-bundle.min.css">

        <link rel="stylesheet" href="assets/css/table.css">
    </head>
    <%
        if (request.getParameter("InvalidKey") != null) {%>
    <script>alert('Invalid Key');</script>  
    <%}
    %>

    <body>


        <!-- ***** Header Area Start ***** -->
        <header class="header-area header-sticky">
            <div class="container">
                <div class="row">
                    <div class="col-12">
                        <nav class="main-nav">
                            <!-- ***** Logo Start ***** -->
                            <a href="#" class="logo">
                            </a>
                            <!-- ***** Logo End ***** -->
                            <!-- ***** Menu Start ***** -->
                            <ul class="nav">
                                <li class="scroll-to-section"><a href="ReceiverHome.jsp">Home</a></li>
                                <li class="scroll-to-section"><a href="SearchFiles.jsp">Search Files</a></li>  
                                <li class="scroll-to-section"><a href="RequestedFiles.jsp">Requested Files</a> </li>        
                                <li class="scroll-to-section"><a href="RTransaction.jsp" class="active">File Transactions</a></li>
                                <li class="scroll-to-section"><a href="logout.jsp">Logout</a></li>

                                <!--                          <li><a href="contact-us.html">Contact Support</a></li> -->
                            </ul>        
                            <a class='menu-trigger'>
                                <span>Menu</span>
                            </a>
                            <!-- ***** Menu End ***** -->
                        </nav>
                    </div>
                </div>
            </div>
        </header>
        <!-- ***** Header Area End ***** -->

        <!-- ***** Main Banner Area Start ***** -->
        <div class="page-heading">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="header-text">
                            <h2>Downloaded File List</h2>
                            <div class="div-dec"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- ***** Main Banner Area End ***** -->

        <section class="contact-us-form">
            <div class="container">
                <div class="row">
                    <table id="naresh" style="margin: auto;padding: 10px;">
                                <tr>
                                    <th>Id</th>
                                    <th>DP Name</th>
                                    <th>File Name</th>
                                    <th>Date & Time</th>
                                </tr>
                                <%
                                    String doid = session.getAttribute("drid").toString();
                                    Connection con = SQLconnection.getconnection();
                                    Statement st = con.createStatement();
                                    try {
                                        ResultSet rs = st.executeQuery("Select * from downloads where drid ='"+doid+"'");
                                        while (rs.next()) {
                                %>
                                <tr>
                                    <td><%=rs.getString("id")%></td>
                                    <td><%=rs.getString("dpname")%></td>
                                    <td><%=rs.getString("fname")%></td>
                                    <td><%=rs.getString("time")%></td>
                                </tr>
                                <%                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                %>
                            </table>

                </div>
            </div>
        </div>
    </section>
    <br><br><br><br><br><br><br><br><br>

    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <p>Copyright © 2023 Sanitizable Access Control System. All Rights Reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- Scripts -->
    <!-- Bootstrap core JavaScript -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

    <script src="assets/js/isotope.min.js"></script>
    <script src="assets/js/owl-carousel.js"></script>

    <script src="assets/js/tabs.js"></script>
    <script src="assets/js/swiper.js"></script>
    <script src="assets/js/custom.js"></script>
</body>
</html>
