import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DNSQuestion {
    // 域名分成的各个部分，例如["www", "example", "com"]
    private final String[] domainParts;
    // 请求的DNS记录类型
    private final int type;
    // DNS记录的类别，通常是IN（互联网）
    private final int recordClass;

    // 构造函数私有化，以便通过工厂方法创建实例，或者根据需要修改为公共
    private DNSQuestion(String[] domainParts, int type, int recordClass) {
        this.domainParts = domainParts;
        this.type = type;
        this.recordClass = recordClass;
    }

    // 从输入流中解码DNS问题的静态工厂方法
    public static DNSQuestion decodeQuestion(InputStream in, DNSMessage message) throws IOException {
        // 使用DNSMessage方法读取域名，处理可能的压缩
        String[] domainParts = message.readDomainName(in);
        // 接下来的两个字节是类型
        int type = readTwoBytesAsInt(in);
        // 接下来的两个字节是类
        int recordClass = readTwoBytesAsInt(in);

        return new DNSQuestion(domainParts, type, recordClass);
    }

    // 从输入流中读取两个字节并转换为int的辅助方法
    private static int readTwoBytesAsInt(InputStream in) throws IOException {
        int highByte = in.read();
        int lowByte = in.read();
        if (highByte == -1 || lowByte == -1) {
            throw new IOException("Unexpected end of stream while reading DNS question");
        }
        return (highByte << 8) + lowByte;
    }

    // 将DNS问题写入ByteArrayOutputStream
    public void writeBytes(ByteArrayOutputStream out, HashMap<String, Integer> domainNameLocations) throws IOException {
        // 写入域名，可能使用压缩
        DNSMessage.writeDomainName(out, domainNameLocations, this.domainParts);
        // 类型的高字节
        out.write((this.type >> 8) & 0xFF);
        // 类型的低字节
        out.write(this.type & 0xFF);
        // 类的高字节
        out.write((this.recordClass >> 8) & 0xFF);
        // 类的低字节
        out.write(this.recordClass & 0xFF);
    }

    @Override
    public String toString() {
        // 重写toString方法，便于输出和调试
        String domainName = String.join(".", this.domainParts);
        return String.format("DNSQuestion{domainName='%s', type=%d, recordClass=%d}", domainName, this.type, this.recordClass);
    }

    // Getter方法
    public String[] getDomainParts() {
        return domainParts;
    }

    public int getType() {
        return type;
    }

    public int getRecordClass() {
        return recordClass;
    }

    // 基于domainParts、type和recordClass实现equals和hashCode，保证在集合中的一致性
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNSQuestion that = (DNSQuestion) o;

        if (type != that.type) return false;
        if (recordClass != that.recordClass) return false;
        return java.util.Arrays.equals(domainParts, that.domainParts);
    }

    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(domainParts);
        result = 31 * result + type;
        result = 31 * result + recordClass;
        return result;
    }
}
