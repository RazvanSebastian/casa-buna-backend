package ro.casabuna.casabuna_backend.access.team;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAssociatedManager {

    String teamIdParam() default "teamId";

    boolean required() default true;
}
