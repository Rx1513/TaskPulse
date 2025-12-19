package tasks;

public enum Status {
    NEW,
    INWORK,
    PAUSED,
    FEEDBACK,
    DONE,
    CLOSED;

    public String getDisplayName() {
        return switch (this) {
            case NEW -> "Новая";
            case INWORK -> "В работе";
            case PAUSED -> "Приостановлена";
            case FEEDBACK -> "Обратная связь";
            case DONE -> "Решена";
            case CLOSED -> "Закрыта";
        };
    }
}
