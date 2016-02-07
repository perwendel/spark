package spark.route;

public enum RedirectStatus {
    MULTIPLE_CHOICES(300),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    SEE_OTHER(303),
    NOT_MODIFIED(304),
    USE_PROXY(305),
    SWITCH_PROXY(306),
    TEMPORARY_REDIRECT(307),
    PERMANENT_REDIRECT(308);

    private int intValue;

    RedirectStatus(int intValue) {
        this.intValue = intValue;
    }
    public int getIntValue() {
        return intValue;
    }
}
