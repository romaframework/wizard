<%@page import="org.romaframework.core.*"%>
<%@page import="org.romaframework.core.schema.SchemaHelper"%>
<%@page import="org.romaframework.aspect.session.*"%>
<%@page import="org.romaframework.aspect.i18n.*"%>
<%@page import="org.romaframework.core.config.ApplicationConfiguration"%>
<%@page import="org.romaframework.aspect.authentication.AuthenticationAspect"%>
<%
    String appName = Utility.getCapitalizedString(Roma.component(ApplicationConfiguration.class).getApplicationName());
    SessionInfo sess = Roma.session().getActiveSessionInfo();
%>
<table width='100%' style="border-bottom: 1px solid #9DBBC6;">
	<tr>
		<td align="left"
			style="width: 150px;height: 75px; background-image: url(<%=request.getContextPath()%>/static/base/image/logo.jpg);background-repeat: no-repeat;background-color: rgb(255, 255, 255);background-position: 20px 5px; border-color: rgb(255, 255, 255);border: 0;"></td>
		<td>
		<h1><a style="text-decoration: none" href='<%=request.getContextPath()%>/app/direct/home'><%=appName%></a></h1>
		</td>
		<td>
		<table>
			<tr>
				<td>User:</td>
				<td><a href='<%=request.getContextPath()%>/app/direct/changePassword'><%=sess.getAccount()%></a></td>
			</tr>
			<tr>
				<td>Logged On:</td>
				<td><%=Roma.i18n().getDateTimeFormat().format(sess.getCreated())%></td>
			</tr>
		</table>
		</td>
		<td align="center"><a href='<%=request.getContextPath()%>/app/direct/home'> <img
			src='<%=request.getContextPath()%>/static/base/image/home.png' border='0' /> </a></td>
		<td align="center"><a href='<%=request.getContextPath()%>/app/direct/changePassword'> <img
			src='<%=request.getContextPath()%>/static/base/image/changepassword.png' border='0' /> </a></td>
		<td align="center"><a href='<%=request.getContextPath()%>/app/direct/controlPanel'> <img
			src='<%=request.getContextPath()%>/static/base/image/controlPanel.png' border='0' /> </a></td>
		<td align="center"><a href='<%=request.getContextPath()%>/dynamic/common/logout.jsp'> <img
			src='<%=request.getContextPath()%>/static/base/image/logout.png' border='0' /> </a></td>
	</tr>
</table>