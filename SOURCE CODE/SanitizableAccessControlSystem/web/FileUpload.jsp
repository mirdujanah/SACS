<%-- 
    Document   : FileUpload
    Created on : Jan 26, 2023, 9:54:18 PM
    Author     : Murthi
--%>
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
        if (request.getParameter("Success") != null) {%>
    <script>alert('File upload Success');</script>  
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
                          <li class="scroll-to-section"><a href="DPHome.jsp">Home</a></li>
                          <li class="scroll-to-section"><a href="FileUpload.jsp" class="active">File Upload</a></li>  
                          <li class="scroll-to-section"><a href="MyFiles.jsp">My Files</a> </li>        
                          <li class="scroll-to-section"><a href="MFTransaction.jsp">File Transactions</a></li>
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
            <h2>Files Publish</h2>
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
        <div class="col-lg-6 offset-lg-3">
          <div class="section-heading">
            <h4>File Upload</h4>
          </div>
        </div>
          <div >
          <form id="contact" action="Upload" method="post" ENCTYPE="multipart/form-data">
            <div class="row offset-lg-3" >
               <div class="col-lg-8">
                <fieldset>
                  <input type="text" name="kword" id="email"  placeholder="Keyword" required="">
                </fieldset>
              </div>
               <div class="col-lg-8">
                <fieldset>
                    <input type="file" name="upfile" id="subject" placeholder="File" autocomplete="on" >
                </fieldset>
              </div>
                <div class="col-lg-8 offset-lg-3">
                <fieldset>
                  <button type="submit" id="form-submit" class="orange-button">Upload</button>
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
  </body>
</html>