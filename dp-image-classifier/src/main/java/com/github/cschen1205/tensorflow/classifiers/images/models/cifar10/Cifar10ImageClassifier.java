package com.github.cschen1205.tensorflow.classifiers.images.models.cifar10;

import com.github.cschen1205.tensorflow.classifiers.images.models.ImageClassifier;
import com.github.cschen1205.tensorflow.classifiers.images.utils.ImageUtils;
import com.github.cschen1205.tensorflow.classifiers.images.utils.InputStreamUtils;
import com.github.cschen1205.tensorflow.classifiers.images.utils.ResourceUtils;
import com.github.cschen1205.tensorflow.classifiers.images.utils.TensorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Cifar10ImageClassifier implements ImageClassifier, AutoCloseable {

    private Graph graph = new Graph();
    public Cifar10ImageClassifier() {

    }

    public void load_model(InputStream inputStream) throws IOException {
        byte[] bytes = InputStreamUtils.getBytes(inputStream);
        graph.importGraphDef(bytes);
    }

    @Override
    public void load_model() throws IOException {
        InputStream inputStream = ResourceUtils.getInputStream("tf_models/cnn_cifar10.pb");
        load_model(inputStream);
    }

    private static final String[] labels = new String[]{
            "airplane",
            "automobile",
            "bird",
            "cat",
            "deer",
            "dog",
            "frog",
            "horse",
            "ship",
            "truck"
    };

    private static final Logger logger = LoggerFactory.getLogger(Cifar10ImageClassifier.class);

    @Override
    public String predict_image(BufferedImage image) {
        return predict_image(image, 32, 32);
    }

    @Override
    public String predict_image(BufferedImage image, int imgWidth, int imgHeight){

        float[] predicted = encode_image(image, imgWidth, imgHeight);
        if(predicted == null) {
            return "unknown";
        }

        int nlabels = predicted.length;

        int argmax = 0;
        float max = predicted[0];
        for(int i=1; i < nlabels; ++i) {
            if(max < predicted[i]) {
                max = predicted[i];
                argmax = i;
            }
        }

        return labels[argmax];
    }

    @Override
    public float[] encode_image(BufferedImage image) {
        return encode_image(image, 32, 32);
    }



    @Override
    public float[] encode_image(BufferedImage image, int imgWidth, int imgHeight) {
        image = ImageUtils.resizeImage(image, imgWidth, imgHeight);

        Tensor<Float> imageTensor = TensorUtils.getImageTensor(image, imgWidth, imgHeight);

        try (Session sess = new Session(graph);
             Tensor<Float> result =
                     sess.runner().feed("conv2d_1_input:0", imageTensor)
                             .feed("dropout_1/keras_learning_phase:0", Tensor.create(false))
                             .fetch("output_node0:0").run().get(0).expect(Float.class)) {
            final long[] rshape = result.shape();
            if (result.numDimensions() != 2 || rshape[0] != 1) {
                throw new RuntimeException(
                        String.format(
                                "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                Arrays.toString(rshape)));
            }
            int nlabels = (int) rshape[1];
            return  result.copyTo(new float[1][nlabels])[0];
        } catch(Exception ex) {
            logger.error("Failed to predict image", ex);
        }

        return null;
    }

    @Override
    public void close() throws Exception {
        if(graph != null) {
            graph.close();
            graph = null;
        }
    }
}
