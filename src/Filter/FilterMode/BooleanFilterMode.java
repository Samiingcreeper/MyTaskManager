package Filter.FilterMode;

public class BooleanFilterMode{

    public static final FilterMode<Boolean> EQUALS = new FilterMode<Boolean>( (value, taskValue) ->{
        return value == taskValue;
    });

    public static final FilterMode<Boolean> NOT_EQUALS = new FilterMode<Boolean>( (value, taskValue) ->{
        return value != taskValue;
    });
}
