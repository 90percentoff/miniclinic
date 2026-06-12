package tw.edu.fju.miniclinic.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import tw.edu.fju.miniclinic.controller.LoginController;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull  HttpServletResponse response,
                             @NonNull  Object handler) throws Exception {

        HttpSession session = request.getSession();
        Object loggedIn = session.getAttribute(LoginController.SESSION_LOGGED_IN_DOCTOR_ID);

        if (loggedIn != null) {
            return true;
        }

        String path = request.getRequestURI();
        if (path.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"請先登入\"}");
        } else {
            response.sendRedirect("/login");
        }

        return false;
    }
}
