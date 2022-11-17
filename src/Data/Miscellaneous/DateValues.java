package Data.Miscellaneous;

import java.time.LocalDate;

public class DateValues{
    private String yearString;
    private String monthString;
    private String dayOfMonthString;

    public DateValues(LocalDate localDate){
        this(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }
    public DateValues(int year, int month, int day){
        this(Integer.toString(year), Integer.toString(month), Integer.toString(day));
    }

    public DateValues(String yearString, String monthString, String dayOfMonthString){
        this.yearString = yearString.trim();
        this.monthString = monthString.trim();
        this.dayOfMonthString = dayOfMonthString.trim();
    }

    public int getYear(){
        return Integer.parseInt(yearString);
    }

    public int getMonth(){
        return Integer.parseInt(monthString);
    }

    public int getDayOfMonth(){
        return Integer.parseInt(dayOfMonthString);
    }

    public LocalDate getLocalDate(){
        return LocalDate.of(getYear(), getMonth(), getDayOfMonth());
    }
}