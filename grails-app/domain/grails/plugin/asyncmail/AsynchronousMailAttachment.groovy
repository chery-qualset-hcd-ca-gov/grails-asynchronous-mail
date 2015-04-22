package grails.plugin.asyncmail

import java.sql.Blob

class AsynchronousMailAttachment implements Serializable {

    static final DEFAULT_MIME_TYPE = 'application/octet-stream'

    private static final SIZE_30_MB = 30*1024*1024

    String attachmentName
    String mimeType = DEFAULT_MIME_TYPE
    byte[] content
    Blob contentBlob
    boolean inline = false

    static belongsTo = [message:AsynchronousMailMessage]
    static transients = ['content']
    static mapping = {
        table 'async_mail_attachment'
        id generator: 'sequence', params: [sequence: 'async_mail_attachment_seq']
        version false
    }

    static constraints = {
        attachmentName(blank:false)
        mimeType()
//        content(maxSize:SIZE_30_MB)
        contentBlob() //maxSize:SIZE_30_MB)
    }

    byte[] getContent() {
        byte[] content = null;

        if (contentBlob) {
            content = getBytesFromBlob(contentBlob)
        }

        return content;
    }
    // BlobUtil
    static byte[] getBytesFromBlob(Blob blob) {
        byte[] blobBytes = null;
        ByteArrayOutputStream baos = null;
        InputStream inputStream = null;

        if (blob) {
            baos = new ByteArrayOutputStream();

            try {
                byte[] buf = new byte[5000];
                inputStream = blob.getBinaryStream();

                while (true) {
                    int dataSize = inputStream.read(buf);

                    if (dataSize == -1) {
                        break;
                    }

                    baos.write(buf, 0, dataSize);
                }

                blobBytes = baos.toByteArray();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                if (baos != null) {
                    try {
                        baos.close();
                    }
                    catch (IOException ex) {
                        // eat it.
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException ex) {
                        // eat it.
                    }
                }
            }
        }

        return blobBytes
    }

}
