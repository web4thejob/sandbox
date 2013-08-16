package job.myprojects.validator;

import job.myprojects.Task;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class NotBillableValidator implements
        ConstraintValidator<NotBillable, Task> {

    @Override
    public void initialize(NotBillable constraintAnnotation) {
        //nothing to do
    }

    @Override
    public boolean isValid(Task task, ConstraintValidatorContext context) {
        boolean valid = isNotBillableValid(task);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("the amount of hours is invalid").addPropertyNode
                    ("notBillable")
                    .addConstraintViolation();
        }

        return valid;
    }

    private boolean isNotBillableValid(Task task) {
        if (task.getNotBillable() != null) {
            task.calculate();
            return task.getDuration().compareTo(task.getNotBillable()) >= 0;
        }
        return true;
    }


}

