<%-- 
    Document   : SanitizedFiles
    Created on : Jan 27, 2023, 2:49:03 PM
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
        if (request.getParameter("Success") != null) {%>
    <script>alert('File Sanitized Successfully');</script>  
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
                          <li class="scroll-to-section"><a href="SanitizerHome.jsp">Home</a></li>
                          <li class="scroll-to-section"><a href="SanitizeFiles.jsp">Sanitize Files</a></li>  
                          <li class="scroll-to-section"><a href="SanitizedFiles.jsp" class="active">Sanitized Files</a> </li>        
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
            <h2>Sanitized Files</h2>
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
        <center><table id="naresh">
                                <tr>
                                    <th>File Id</th>
                                    <th>Data Publisher Id</th>
                                    <th>Data Name</th>
                                    <th>File Name</th>
                                    <th>Uploaded Time</th>
                                    <th>Status</th>
                                </tr>
                                <%
                                    Connection con = SQLconnection.getconnection();
                                    Statement st = con.createStatement();
                                    try {
                                        ResultSet rs = st.executeQuery("Select * from uploads where fstatus='Sanitized' ");
                                        while (rs.next()) {
                                %>
                                <tr>
                                    <td><%=rs.getString("id")%></td>
                                    <td><%=rs.getString("dpid")%></td>
                                    <td><%=rs.getString("dpname")%></td>
                                    <td><%=rs.getString("fname")%></td>
                                    <td><%=rs.getString("time")%></td>
                                    <td><%=rs.getString("fstatus")%></td>
                                </tr>
                                <%                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                %>
                            </table></center> 
                           
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