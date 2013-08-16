package job.myprojects.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = NotBillableValidator.class)
@Documented
public @interface NotBillable {

    String message() default "the amount of hours is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
