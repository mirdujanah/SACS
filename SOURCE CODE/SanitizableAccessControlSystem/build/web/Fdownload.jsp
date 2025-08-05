<%-- 
    Document   : Fdownload
    Created on : Jan 27, 2023, 6:54:33 PM
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
    
    
  </head>
    <%
        if (request.getParameter("Requestsent") != null) {%>
    <script>alert('Request Sent');</script>  
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
                          <li class="scroll-to-section"><a href="SearchFiles.jsp" class="active">Search Files</a></li>  
                          <li class="scroll-to-section"><a href="RequestedFiles.jsp">Requested Files</a> </li>        
                          <li class="scroll-to-section"><a href="RTransaction.jsp">File Transactions</a></li>
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
            <h2>Download File</h2>
            <div class="div-dec"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
<%
            String drid = session.getAttribute("drid").toString();
            String fname = request.getParameter("fname");
            String fid = request.getParameter("fid");
            String rid = request.getParameter("rid");
            System.out.println("rid "+rid);
            System.out.println("fid "+fid);
            System.out.println("fname "+fname);
            System.out.println("drid "+drid);

            Connection con = SQLconnection.getconnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("Select * from searchreq where id ='" + rid + "' and vstatus='Approved'");
            if(rs.next()==false) {
            response.sendRedirect("RequestedFiles.jsp?NotApproved");
            rs.close();
            }


        %>
  <!-- ***** Main Banner Area End ***** -->

  <section class="contact-us-form">
    <div class="container">
      <div class="row">
        <div class="col-lg-6 offset-lg-3">
          <div class="section-heading">
            <h4>Key Verification</h4>
          </div>
        </div>
          <div >
          <form id="contact" action="Download" method="post" >
            <div class="row offset-lg-3" >
               <div class="col-lg-8">
                <fieldset>
                  <input type="text" value="<%=fname%>" readonly="" id="email"  placeholder="Keyword" required="">
                </fieldset>
              </div>
               <div class="col-lg-8">
                <fieldset>
                    <input type="text" id="email" name="dkey" placeholder="Secret Key" autocomplete="off" required="">
                </fieldset>
              </div>
                 <input type="hidden" value="<%=fid%>" name="fid" >
                <div class="col-lg-8 offset-lg-3">
                <fieldset>
                  <button type="submit" id="form-submit" class="orange-button">Get File</button>
                </fieldset>
              </div>
                
            </div>
           </form>
        </div>
      </div>
    </div>
  </section>
  <br><br><br>

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
    <script>
        var interleaveOffset = 0.5;

      var swiperOptions = {
        loop: true,
        speed: 1000,
        grabCursor: true,
        watchSlidesProgress: true,
        mousewheelControl: true,
        keyboardControl: true,
        navigation: {
          nextEl: ".swiper-button-next",
          prevEl: ".swiper-button-prev"
        },
        on: {
          progress: function() {
            var swiper = this;
            for (var i = 0; i < swiper.slides.length; i++) {
              var slideProgress = swiper.slides[i].progress;
              var innerOffset = swiper.width * interleaveOffset;
              var innerTranslate = slideProgress * innerOffset;
              swiper.slides[i].querySelector(".slide-inner").style.transform =
                "translate3d(" + innerTranslate + "px, 0, 0)";
            }      
          },
          touchStart: function() {
            var swiper = this;
            for (var i = 0; i < swiper.slides.length; i++) {
              swiper.slides[i].style.transition = "";
            }
          },
          setTransition: function(speed) {
            var swiper = this;
            for (var i = 0; i < swiper.slides.length; i++) {
              swiper.slides[i].style.transition = speed + "ms";
              swiper.slides[i].querySelector(".slide-inner").style.transition =
                speed + "ms";
            }
          }
        }
      };

      var swiper = new Swiper(".swiper-container", swiperOptions);
    </script>
  </body>
</html>

