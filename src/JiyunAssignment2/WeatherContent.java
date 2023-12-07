package JiyunAssignment2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class WeatherContent {
    private String id;

    private String name;

    private String state;

    private String time_zone;

    private float lat = -1000;

    private float lon = -1000;

    private String local_date_time;

    private String local_date_time_full;

    private float air_temp = -1000;

    private float apparent_t = -1000;

    private String cloud;

    private float dewpt = -1000;

    private float press = -1000;

    private float rel_hum = -1000;

    private String wind_dir;

    private float wind_spd_kmh = -1000;

    private float wind_spd_kt = -1000;

    public String getId() {
        return id;
    }

    public float getAir_temp() {
        return air_temp;
    }

    public float getApparent_t() {
        return apparent_t;
    }

    public float getDewpt() {
        return dewpt;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public float getPress() {
        return press;
    }

    public float getRel_hum() {
        return rel_hum;
    }

    public float getWind_spd_kmh() {
        return wind_spd_kmh;
    }

    public float getWind_spd_kt() {
        return wind_spd_kt;
    }

    public String getCloud() {
        return cloud;
    }

    public String getLocal_date_time() {
        return local_date_time;
    }

    public String getLocal_date_time_full() {
        return local_date_time_full;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWind_spd_kt(String wind_spd_kt) {
        this.wind_spd_kt = Float.parseFloat(wind_spd_kt);
    }

    public void setWind_spd_kmh(String wind_spd_kmh) {
        this.wind_spd_kmh = Float.parseFloat(wind_spd_kmh);
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRel_hum(String rel_hum) {
        this.rel_hum = Float.parseFloat(rel_hum);
    }

    public void setPress(String press) {
        this.press = Float.parseFloat(press);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLon(String lon) {
        this.lon = Float.parseFloat(lon);
    }

    public void setLocal_date_time_full(String local_date_time_full) {
        this.local_date_time_full = local_date_time_full;
    }

    public void setLocal_date_time(String local_date_time) {
        this.local_date_time = local_date_time;
    }

    public void setLat(String lat) {
        this.lat = Float.parseFloat(lat);
    }

    public void setDewpt(String dewpt) {
        this.dewpt = Float.parseFloat(dewpt);
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public void setApparent_t(String apparent_t) {
        this.apparent_t = Float.parseFloat(apparent_t);
    }

    public void setAir_temp(String air_temp) {
        this.air_temp = Float.parseFloat(air_temp);
    }

    public static WeatherContent init(File file) throws IOException {
        String file1 = FileUtils.readFileToString(file);
        JSONObject jsonobject = JSON.parseObject(file1);
        WeatherContent weather = JSON.toJavaObject(jsonobject, WeatherContent.class);
        return weather;
    }
    public String toString() {
        return "id:" + id + "\n" +
                "name:" + name + "\n" +
                "state:" + state + "\n" +
                "time_zone:" + time_zone + "\n" +
                "lat:" + lat + "\n" +
                "lon:" + lon + "\n" +
                "local_date_time:" + local_date_time + "\n" +
                "local_date_time_full:" + local_date_time_full + "\n" +
                "air_temp:" + air_temp + "\n" +
                "apparent_t:" + apparent_t + "\n" +
                "cloud:" + cloud + "\n" +
                "dewpt:" + dewpt + "\n" +
                "press:" + press + "\n" +
                "rel_hum:" + rel_hum + "\n" +
                "wind_dir:" + wind_dir + "\n" +
                "wind_spd_kmh:" + wind_spd_kmh + "\n" +
                "wind_spd_kt:" + wind_spd_kt;
    }
    public static boolean isValidWeatherContent(String json) {
        WeatherContent weatherContent = JSON.parseObject(json, WeatherContent.class);
        if (weatherContent.getId() ==null || weatherContent.getApparent_t() == -1000 || weatherContent.getAir_temp() == -1000 || weatherContent.getDewpt() == -1000 || weatherContent.getLat() == -1000 || weatherContent.getLon() == -1000 || weatherContent.getPress() == -1000 || weatherContent.getRel_hum() == -1000 || weatherContent.getWind_spd_kmh() == -1000 || weatherContent.getWind_spd_kt() == -1000 ||
                weatherContent.getName() == null || weatherContent.getCloud() == null || weatherContent.getLocal_date_time_full() == null || weatherContent.getLocal_date_time() == null || weatherContent.getState() == null || weatherContent.getTime_zone() == null || weatherContent.getWind_dir() == null) {
            return false;
        }
        return true;
    }
}

