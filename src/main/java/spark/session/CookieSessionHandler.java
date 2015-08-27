package spark.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public final class CookieSessionHandler {

    private static final String session = "session";
    private static final Gson gson = new GsonBuilder().create();

    private CookieSessionHandler() {}

    public static CookieSession readSession(HttpServletRequest request) throws IOException {
        Cookie sessionCookie = getSessionCookie(request);
        if (sessionCookie == null) {
            return new CookieSession(request);
        }

        String encodedContent = sessionCookie.getValue();
        byte[] decodedContent = Base64.getDecoder().decode(encodedContent);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedContent);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        InputStreamReader streamReader = new InputStreamReader(gzipInputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        String objectString = new String(reader.readLine().getBytes(), "UTF-8");

        CookieSession session = gson.fromJson(objectString, CookieSession.class);

        session.setRequest(request);

        return session;
    }

    public static void writeSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieSession session = (CookieSession) request.getSession();
        String jsonSession = gson.toJson(session);

        byte[] bytes = jsonSession.getBytes("UTF-8");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        gzipOutputStream.write(bytes);
        gzipOutputStream.flush();
        gzipOutputStream.close();

        String base64EncodedString = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        response.addCookie(new Cookie(CookieSessionHandler.session, base64EncodedString));
    }

    private static Cookie getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (session.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
