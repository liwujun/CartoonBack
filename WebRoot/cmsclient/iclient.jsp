<%@ page language="java" import="java.util.*,com.cms.client.CmsServlet"
	pageEncoding="utf-8"%>
<%
	request.setCharacterEncoding("utf-8");
	//System.out.println(request.getParameter("method"));
	//System.out.println(request.getParameter("xml"));
	//platform(windows,linux) windows无需转码，linux需要转码
	//request.setAttribute("platform","linux");
	CmsServlet cmsServlet = new CmsServlet();
	cmsServlet.execute(request, response);
%>