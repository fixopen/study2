package com.baremind;

import com.baremind.data.Image;
import com.baremind.data.Media;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

@Path("medias")
public class Medias {
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCSV(@Context HttpServletRequest request, @CookieParam("userId") String userId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            try {
                Part p = request.getPart("file");
                String contentType = p.getContentType();
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = contentType.substring(contentType.lastIndexOf("/") + 1);
                if (!Objects.equals(postfix, "jpeg") || !Objects.equals(postfix, "gif") || !Objects.equals(postfix, "ai") || !Objects.equals(postfix, "png")) {
                    String fileName = now + "." + postfix;
                    String pyshicalpath = Properties.getPropertyValue("physicalpath");
                    String uploadedFileLocation = pyshicalpath + fileName;

                    File file = new File(uploadedFileLocation);
                    FileOutputStream w = new FileOutputStream(file);
                    CharacterEncodingFilter.saveFile(w, inputStream);

                    Image image = new Image();
                    image.setId(IdGenerator.getNewId());
                    image.setExt(postfix);
                    image.setMimeType(contentType);
                    image.setName(fileName);
                    image.setSize(p.getSize());
                    String virtualPath = Properties.getPropertyValue("virtualpath") + fileName;
                    image.setStorePath(virtualPath);
                    JPAEntry.genericPost(image);

                    result = Response.ok(new Gson().toJson(image)).build();
                } else {
                    result = Response.status(415).build();
                    //上传图片的格式不正确
                }
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMedia(@CookieParam("userId") String userId, Media media) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            media.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(media);
            result = Response.ok(media).build();
        }
        return result;
    }

    public static Random random = new Random();

    private static int rand(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    @GET //根据条件查询
    @Path("validation-picture")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValidationPicture() throws IOException {
        int w = 120;
        int h = 50;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(new Color(rand(50, 250), rand(50, 250), rand(50, 250)));
        g.fillRect(0, 0, w, h);

        String s = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";
        String validationCode = "";
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(rand(50, 180), rand(50, 180), rand(50, 180)));
            g.setFont(new Font("黑体", Font.PLAIN, 40));
            char c = s.charAt(random.nextInt(s.length()));
            g.drawString(String.valueOf(c), 10 + i * 30, rand(h - 30, h));
            validationCode += c;
        }
        for (int i = 0; i < 25; i++) {
            g.setColor(new Color(rand(50, 180), rand(50, 180), rand(50, 180)));
            g.drawLine(rand(0, w), rand(0, h), rand(0, w), rand(0, h));
        }
        String pyshicalPath = "/Users/fixopen/IdeaProjects/study/target/study/images/";
        String fileName = pyshicalPath + "vcode.png";
        File file = new File(fileName);
        ImageIO.write(img, "png", file);
        String path = "/images/" + "vcode.png";
        return Response.ok(new Gson().toJson(validationCode + "/" + path)).build();
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedias(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Media> medias = JPAEntry.getList(Media.class, filterObject);
            if (!medias.isEmpty()) {
                result = Response.ok(new Gson().toJson(medias)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Media media = JPAEntry.getObject(Media.class, "id", id);
            if (media != null) {
                result = Response.ok(new Gson().toJson(media)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedia(@CookieParam("userId") String userId, @PathParam("id") Long id, Media media) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            Media existmedia = JPAEntry.getObject(Media.class, "id", id);
            if (existmedia != null) {
                String ext = media.getExt();
                if (ext != null) {
                    existmedia.setExt(ext);
                }

                String mimeType = media.getMimeType();
                if (mimeType != null) {
                    existmedia.setMimeType(mimeType);
                }

                String name = media.getName();
                if (name != null) {
                    existmedia.setName(name);
                }

                Long size = media.getSize();
                if (size != null) {
                    existmedia.setSize(size);
                }

                String storePath = media.getStorePath();
                if (storePath != null) {
                    existmedia.setStorePath(storePath);
                }
                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }
}
