package job.myprojects.validator;

import job.myprojects.Task;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class ReasonableTimeValidator implements
        ConstraintValidator<ReasonableTime, Task> {

    @Override
    public void initialize(ReasonableTime constraintAnnotation) {
        //nothing to do
    }

    @Override
    public boolean isValid(Task task, ConstraintValidatorContext context) {
        boolean valid = true;

        if (!isStartTimeValid(task)) {
            valid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("start time is invalid").addPropertyNode("startTime")
                    .addConstraintViolation();
        }

        if (!isEndTimeValid(task)) {
            valid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("end time is invalid").addPropertyNode("endTime")
                    .addConstraintViolation();
        }

        return valid;
    }

    private boolean isStartTimeValid(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            return task.getStartTime().before(task.getEndTime());
        }
        return true;
    }

    private boolean isEndTimeValid(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            return task.getEndTime().after(task.getStartTime());
        }
        return true;
    }
}

