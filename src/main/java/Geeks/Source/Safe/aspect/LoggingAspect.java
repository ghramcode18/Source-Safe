package Geeks.Source.Safe.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut to target all methods in the controller package and sub-packages
    @Pointcut("execution(* Geeks.Source.Safe.controller..*(..))")
    public void controllerMethods() {}

    // Log method arguments (inputs) before execution
    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        // Log method name
        String methodName = joinPoint.getSignature().toShortString();

        // Log method arguments
        Object[] args = joinPoint.getArgs();
        StringBuilder argsStr = new StringBuilder();
        for (Object arg : args) {
            argsStr.append(arg).append(", ");
        }

        logger.info("Before Method: {} | Arguments: [{}]", methodName, argsStr.toString());
    }

    // Log method return value (outputs) after execution
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        // Log method name
        String methodName = joinPoint.getSignature().toShortString();
        // Log method arguments
        Object[] args = joinPoint.getArgs();
        StringBuilder argsStr = new StringBuilder();
        for (Object arg : args) {
            argsStr.append(arg).append(", ");
        }
        // Log return value
        logger.info("After Method: {} | Return: {}", methodName, result, argsStr.toString());
    }
}
