package org.javacode.employee_accounting.security.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.javacode.employee_accounting.exception.InvalidJwtTokenException;
import org.javacode.employee_accounting.security.JwtUtil;
import org.javacode.employee_accounting.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
    private final JwtUtil jwtUtil;
    private final EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Метод, выполняемый для каждого HTTP запроса  
    @SneakyThrows
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
  
        // Шаг 1: Извлечение заголовка авторизации из запроса
        final String authHeader = request.getHeader("Authorization");

        // Шаг 2: Проверка наличия заголовка авторизации  
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        // Шаг 3: Извлечение токена из заголовка
        String jwtToken = authHeader.substring(7);

        // Шаг 4: Извлечение имени пользователя из JWT токена
        String username = null;
        try {
            username = jwtUtil.extractUsername(jwtToken);
        } catch (JwtException e) {
            throw new InvalidJwtTokenException(e.getMessage());
        }

        // Шаг 5: Проверка валидности токена и аутентификации
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = employeeService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
                // Шаг 6: Создание нового контекста безопасности
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);

                logger.info(String.format("%s authenticated", username));
            }
        }
        // Шаг 7: Передача запроса на дальнейшую обработку в фильтрующий цепочке
        filterChain.doFilter(request, response);

    }
  
}