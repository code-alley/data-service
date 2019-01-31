package kr.co.inslab.codealley.dataservice.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.inslab.codealley.dataservice.common.Parameter;
import kr.co.inslab.codealley.dataservice.log.SLog;
import kr.co.inslab.codealley.dataservice.provider.ToolsHandler;

/**
 * Servlet implementation class ToolsDataServlet
 */
@WebServlet("/ToolsDataServlet")
public class ToolsDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ToolsDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SLog.d(this.getServletName() + " : doGet");
		
		String result = ""; //json ��� ���� ����
		
		if (request.getParameter(Parameter.KEY_API_LIST) != null){
			// api list
		}
		else if(request.getParameter(Parameter.KEY_TOOL_TYPE) != null 
				&& request.getParameter(Parameter.KEY_TOOL_NAME) != null 
				&& request.getParameter(Parameter.KEY_TOOL_URL) != null
				&& request.getParameter(Parameter.KEY_API) != null){
			// provider handler 
			ToolsHandler handler = new ToolsHandler();
			handler.process(request, response);
		}
		else	// invalid request!!!
		{
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
