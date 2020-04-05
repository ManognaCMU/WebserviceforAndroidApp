<%--
  User: Sai Manogna Pentyala
  Andrew: spentyal
  Date: 04/04/2020
  Task: Project 4 Task 2
  Last Modified: April 4, 2020
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Web Service Logging and Analysis Dashboard</title>
</head>
<body>
<center>
    <table bgcolor="#75ADE3" border="4">
        <tr>
            <th><b>RESTAURANT FINDER LOGGING AND OPERATIONAL ANALYTICS DASHBOARD</b></th>
        </tr>
    </table>
</center>
<br>
<br>
<center>
    <table bgcolor="#ECA996" border="4">
        <tr>
            <th><b>RESTAURANT FINDER OPERATIONAL ANALYTICS</b></th>
        </tr>
    </table>
</center>
<br>
<br>
<%-- table to store the total number of searches --%>
<table bgcolor="#DDD1F3" border="4">
    <tr>
        <th width=60%>Total Number of Valid Searches</th>
    </tr>
    <% if (request.getAttribute("userSearchCount") != null) {%>
    <tr>
        <td>
            <center><%= request.getAttribute("userSearchCount")%>
            </center>
        </td>
    </tr>
    <% } %>
</table>
<br>
<%-- table to store the top 5 user searches --%>
<table bgcolor="#DDD1F3" border="4">
    <tr>
        <th colspan="4">Top 5 Valid User Searches</th>
    </tr>
    <tr>
        <th>S.No.</th>
        <th>City Name</th>
        <th>State Name</th>
        <th>Count</th>
    </tr>
    <% if (request.getAttribute("operationalAnalytics_Part1") != null) {%>
    <% Map<String, Integer> inputMap = (Map<String, Integer>) request.getAttribute("operationalAnalytics_Part1"); %>
    <tr>
        <% int i = 0; %>
        <% for (Map.Entry<String, Integer> hmap : inputMap.entrySet()) { %>
        <% String searchWord = hmap.getKey(); %>
        <% int count = hmap.getValue(); %>
        <% if (i > 5) break; %>
        <%-- serial no --%>
        <td width="25%">
            <center><%= ++i %>
            </center>
        </td>
        <%-- city name --%>
        <td width="25%"><%= searchWord.split("\\$")[0]%>
        </td>
        <%-- state name --%>
        <td width="25%"><%= searchWord.split("\\$")[1]%>
        </td>
        <%-- count--%>
        <td width="25%">
            <center><%= count%>
            </center>
        </td>
    </tr>
    <tr>
        <% }
        } %>
    </tr>
</table>
<br>
<br>
<%-- table to store the average search latency --%>
<table bgcolor="#DDD1F3" border="4">
    <tr>
        <th width=60%>Average Restaurant Search Latency (ms)</th>
    </tr>
    <% if (request.getAttribute("averageLatency_Part1") != null) {%>
    <tr>
        <td>
            <center>Retrieve Restaurant: <%= request.getAttribute("averageLatency_Part1")%>
            </center>
        </td>
    </tr>
    <% } %>
    <% if (request.getAttribute("averageLatency_Part2") != null) {%>
    <tr>
        <td>
            <center>Retrieve Restaurant Photos: <%= request.getAttribute("averageLatency_Part2")%>
            </center>
        </td>
    </tr>
    <% } %>
</table>
<br>
<%-- table to store the distinct mobile phones --%>
<table bgcolor="#DDD1F3" border="4">
    <tr>
        <th colspan="4"> Distinct Mobile Phones</th>
    </tr>
    <tr>
        <th>S.No.</th>
        <th>Mobile Phone (User Agent)</th>
    </tr>
    <% if (request.getAttribute("actualInputList_Part3") != null) {%>
    <% Set<String> inputSet = (Set<String>) request.getAttribute("actualInputList_Part3"); %>
    <tr>
        <% int i = 0; %>
        <% for (String userAgent : inputSet) { %>
        <%-- stores the serial number --%>
        <td>
            <center><%= ++i %>
            </center>
        </td>
        <%-- stores the user agent --%>
        <td><%= userAgent%>
        </td>
    </tr>
    <tr>
        <% }
        } %>
    </tr>
</table>
<br>
<br>
<br>
<center>
    <table bgcolor="#ECA996" border="4">
        <tr>
            <th><b>RESTAURANT FINDER LOGGING</b></th>
        </tr>
    </table>
</center>
<br>
<br>
<%-- table to log the restaurant details --%>
<table style="width:100%" bgcolor="#DDD1F3" border="4" width = 100%>
    <tr>
        <th colspan="8">Restaurant Finder Logging - Retrieve Restaurant</th>
    </tr>
    <tr>
        <th>User Request</th>
        <th>Request Timestamp</th>
        <th>Response Timestamp</th>
        <th>Latency (ms)</th>
        <th>Phone Model (User Agent)</th>
        <th>Request to Zomato API</th>
        <th>Response sent to the Phone - Restaurant List</th>
    </tr>
    <% if (request.getAttribute("actualInputList_Part1") != null) {%>
    <% List<String> inputList = (List<String>) request.getAttribute("actualInputList_Part1"); %>
    <tr>
        <% for (String inputStr : inputList) { %>
        <%-- stores the city name and state name --%>
        <td width="10%">City Name: <%= inputStr.split("\\$")[0]%>; State Name: <%= inputStr.split("\\$")[1]%>
        </td>
        <%-- stores the request timestamp --%>
        <td width="10%"><%= inputStr.split("\\$")[2]%>
        </td>
        <%-- stores the response timestamp --%>
        <td width="10%"><%= inputStr.split("\\$")[3]%>
        </td>
        <%-- stores the latency --%>
        <td width="5%"><%= inputStr.split("\\$")[4]%>
        </td>
        <%-- stores the user agent --%>
        <td width="15%"><%= inputStr.split("\\$")[5]%>
        </td>
        <%-- stores the request to zomato api --%>
        <td width="29%"><%= inputStr.split("\\$")[6]%>
        </td>
        <%-- stores the response to android phone --%>
        <td width="30%"><%= inputStr.split("\\$")[7]%>
        </td>
    </tr>
    <tr>
        <% }
        } %>
    </tr>
</table>
<br>
<br>
<%-- table to log the restaurant images details --%>
<table style="width:100%" bgcolor="#DDD1F3" border="4" width="100%" layout="fixed">
    <tr>
        <th colspan="7">Restaurant Finder Logging - Retrieve Restaurant Photos</th>
    </tr>
    <tr>
        <th>User Request</th>
        <th>Request Timestamp</th>
        <th>Request Timestamp</th>
        <th>Latency</th>
        <th>Phone Model (User Agent)</th>
        <th>Request to Zomato API</th>
        <th>Response sent to the Phone - Restaurant Photos URL List</th>
    </tr>
    <% if (request.getAttribute("actualInputList_Part2") != null) {%>
    <% List<String> inputList = (List<String>) request.getAttribute("actualInputList_Part2"); %>
    <tr>
        <% for (String inputStr : inputList) { %>
        <%-- stores the city name, state name and restaurant name --%>
        <td width="10%">City Name: <%= inputStr.split("\\$")[0]%>; State Name: <%= inputStr.split("\\$")[1]%>;
            Restaurant Name: <%= inputStr.split("\\$")[2]%>
        </td>
        <%-- stores the request timestamp --%>
        <td width="10%"><%= inputStr.split("\\$")[3]%>
        </td>
        <%-- stores the response timestamp --%>
        <td width="10%"><%= inputStr.split("\\$")[4]%>
        </td>
        <%-- stores the latency --%>
        <td width="5%"><%= inputStr.split("\\$")[5]%>
        </td>
        <%-- stores the user agent --%>
        <td width="15%"><%= inputStr.split("\\$")[6]%>
        </td>
        <%-- stores the request to zomato api --%>
        <td width="29%"><%= inputStr.split("\\$")[7]%>
        </td>
        <%-- stores the response to android phone --%>
        <td width="30%"><%= inputStr.split("\\$")[8]%>
        </td>
    </tr>
    <tr>
        <% }
        } %>
    </tr>
</table>
</body>
</html>
