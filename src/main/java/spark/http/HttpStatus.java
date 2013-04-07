package spark.http;

/**
 * Interface Used to unbind Response api from concrete Enum.
 */
public interface HttpStatus {

    final static int NOT_SET_000 = 0;
    final static int CONTINUE_100 = 100;
    final static int SWITCHING_PROTOCOLS_101 = 101;
    final static int PROCESSING_102 = 102;

    final static int OK_200 = 200;
    final static int CREATED_201 = 201;
    final static int ACCEPTED_202 = 202;
    final static int NON_AUTHORITATIVE_INFORMATION_203 = 203;
    final static int NO_CONTENT_204 = 204;
    final static int RESET_CONTENT_205 = 205;
    final static int PARTIAL_CONTENT_206 = 206;
    final static int MULTI_STATUS_207 = 207;

    final static int MULTIPLE_CHOICES_300 = 300;
    final static int MOVED_PERMANENTLY_301 = 301;
    final static int MOVED_TEMPORARILY_302 = 302;
    final static int FOUND_302 = 302;
    final static int SEE_OTHER_303 = 303;
    final static int NOT_MODIFIED_304 = 304;
    final static int USE_PROXY_305 = 305;
    final static int TEMPORARY_REDIRECT_307 = 307;

    final static int BAD_REQUEST_400 = 400;
    final static int UNAUTHORIZED_401 = 401;
    final static int PAYMENT_REQUIRED_402 = 402;
    final static int FORBIDDEN_403 = 403;
    final static int NOT_FOUND_404 = 404;
    final static int METHOD_NOT_ALLOWED_405 = 405;
    final static int NOT_ACCEPTABLE_406 = 406;
    final static int PROXY_AUTHENTICATION_REQUIRED_407 = 407;
    final static int REQUEST_TIMEOUT_408 = 408;
    final static int CONFLICT_409 = 409;
    final static int GONE_410 = 410;
    final static int LENGTH_REQUIRED_411 = 411;
    final static int PRECONDITION_FAILED_412 = 412;
    final static int REQUEST_ENTITY_TOO_LARGE_413 = 413;
    final static int REQUEST_URI_TOO_LONG_414 = 414;
    final static int UNSUPPORTED_MEDIA_TYPE_415 = 415;
    final static int REQUESTED_RANGE_NOT_SATISFIABLE_416 = 416;
    final static int EXPECTATION_FAILED_417 = 417;
    final static int UNPROCESSABLE_ENTITY_422 = 422;
    final static int LOCKED_423 = 423;
    final static int FAILED_DEPENDENCY_424 = 424;

    final static int INTERNAL_SERVER_ERROR_500 = 500;
    final static int NOT_IMPLEMENTED_501 = 501;
    final static int BAD_GATEWAY_502 = 502;
    final static int SERVICE_UNAVAILABLE_503 = 503;
    final static int GATEWAY_TIMEOUT_504 = 504;
    final static int HTTP_VERSION_NOT_SUPPORTED_505 = 505;
    final static int INSUFFICIENT_STORAGE_507 = 507;

    public static final int MAX_CODE = 507;

    int getCode();

    String getMessage();
}
