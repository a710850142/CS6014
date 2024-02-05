import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class DNSRecord {
    // 分解的域名，例如["www", "example", "com"]
    private final String[] domainParts;
    // DNS记录的类型（例如，A、MX）
    private final int type;
    // 记录的类别，通常是IN（互联网）
    private final int recordClass;
    // 生存时间，以秒为单位
    private final long ttl;
    // 记录数据（例如，对于A记录是IP地址）
    private final byte[] rdata;
    // 记录创建的时间戳
    private final Date creationDate;

    // 构造函数初始化所有字段
    private DNSRecord(String[] domainParts, int type, int recordClass, long ttl, byte[] rdata) {
        this.domainParts = domainParts;
        this.type = type;
        this.recordClass = recordClass;
        this.ttl = ttl;
        this.rdata = rdata;
        this.creationDate = new Date(); // 将创建日期设置为当前时间
    }

    // 从InputStream解码DNS记录
    public static DNSRecord decodeRecord(InputStream in, DNSMessage message) throws IOException {
        // 解码域名，处理可能的压缩
        String[] domainParts = message.readDomainName(in);
        // 读取类型和类别
        int type = readTwoBytesAsInt(in);
        int recordClass = readTwoBytesAsInt(in);
        // 读取TTL
        long ttl = readFourBytesAsLong(in);
        // 读取RDATA长度，并读取RDATA
        int rdataLength = readTwoBytesAsInt(in);
        byte[] rdata = new byte[rdataLength];
        if (in.read(rdata) != rdataLength) {
            throw new IOException("Incomplete read of RDATA");
        }

        return new DNSRecord(domainParts, type, recordClass, ttl, rdata);
    }

    // 从InputStream中读取两个字节并转换为int
    private static int readTwoBytesAsInt(InputStream in) throws IOException {
        int highByte = in.read();
        int lowByte = in.read();
        if (highByte == -1 || lowByte == -1) {
            throw new IOException("Unexpected end of stream");
        }
        return (highByte << 8) + lowByte;
    }

    // 从InputStream中读取四个字节并转换为long
    private static long readFourBytesAsLong(InputStream in) throws IOException {
        long result = 0;
        for (int i = 3; i >= 0; i--) {
            int byteValue = in.read();
            if (byteValue == -1) {
                throw new IOException("Unexpected end of stream");
            }
            result |= ((long) byteValue & 0xFF) << (i * 8);
        }
        return result;
    }

    // 将DNS记录编码到ByteArrayOutputStream
    public void writeBytes(ByteArrayOutputStream out, HashMap<String, Integer> domainNameLocations) throws IOException {
        // 编码域名、类型、类别、TTL和RDATA
        DNSMessage.writeDomainName(out, domainNameLocations, this.domainParts);
        out.write((this.type >> 8) & 0xFF);
        out.write(this.type & 0xFF);
        out.write((this.recordClass >> 8) & 0xFF);
        out.write(this.recordClass & 0xFF);
        out.write((int) ((this.ttl >> 24) & 0xFF));
        out.write((int) ((this.ttl >> 16) & 0xFF));
        out.write((int) ((this.ttl >> 8) & 0xFF));
        out.write((int) (this.ttl & 0xFF));
        out.write((this.rdata.length >> 8) & 0xFF);
        out.write(this.rdata.length & 0xFF);
        out.write(this.rdata);
    }

    // 判断记录是否过期
    public boolean isExpired() {
        long elapsedTime = (new Date().getTime() - this.creationDate.getTime()) / 1000;
        return elapsedTime > this.ttl;
    }

    @Override
    public String toString() {
        // 重写toString方法便于输出和调试
        String domainName = String.join(".", this.domainParts);
        return String.format("DNSRecord{domainName='%s', type=%d, recordClass=%d, ttl=%d, rdata=%s}", domainName, this.type, this.recordClass, this.ttl, java.util.Arrays.toString(this.rdata));
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

    public long getTtl() {
        return ttl;
    }

    public byte[] getRdata() {
        return rdata;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
