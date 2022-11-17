package Filter.FilterMode;

import java.time.*;

public class LocalDateFilterMode{

    private static final long MILLI_OF_A_DAY = 24 * 60 * 60 * 1000;

    public static final FilterMode<LocalDate> AT_THE_SAME_DAY = new FilterMode<LocalDate>((value, taskValue) ->{
        return value.isEqual(taskValue);
    });

    public static FilterMode<LocalDate> isAfterAndWithinDays(int days){
        return new FilterMode<LocalDate>((date, taskDate) ->{

           long epochMilliTaskDate = toEpochMilli(taskDate);
           long epochMilliDate = toEpochMilli(date);

           boolean isAfter = epochMilliTaskDate >= epochMilliDate;
           boolean isWithinDays = epochMilliTaskDate - epochMilliDate <= days * MILLI_OF_A_DAY;
           return isAfter && isWithinDays;
        });
    }

    public static FilterMode<LocalDate> isAfterDays(int days){
        return new FilterMode<LocalDate>((date, taskDate) ->{

            long epochMilliTaskDate = toEpochMilli(taskDate);
            long epochMilliDate = toEpochMilli(date);

            return epochMilliTaskDate - epochMilliDate == days * MILLI_OF_A_DAY;
        });
    }


    private static long toEpochMilli(LocalDate localDate){
        LocalDateTime localDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli();
    }
}
