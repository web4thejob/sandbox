package job.myprojects.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ReasonableTimeValidator.class)
@Documented
public @interface ReasonableTime {

    String message() default "Start nd End time are invalid (Never uses actually)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
