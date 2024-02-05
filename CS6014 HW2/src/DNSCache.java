import java.util.concurrent.ConcurrentHashMap;

public class DNSCache {
    // 使用ConcurrentHashMap来存储DNS响应数据，确保线程安全
    private static final ConcurrentHashMap<String, byte[]> cache = new ConcurrentHashMap<>();

    // 私有构造函数防止实例化
    private DNSCache() {
        // Private constructor to prevent instantiation
    }

    // 将DNS查询的响应数据存储到缓存中
    public static void put(String key, byte[] response) {
        cache.put(key, response);
    }

    // 根据DNS查询的键（如查询的域名）从缓存中获取响应数据
    public static byte[] get(String key) {
        return cache.get(key);
    }

    // 清空缓存中的所有响应数据
    public static void clear() {
        cache.clear();
    }

    // 获取缓存中存储的响应数据的数量
    public static int size() {
        return cache.size();
    }
}
