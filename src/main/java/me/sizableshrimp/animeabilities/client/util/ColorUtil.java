package me.sizableshrimp.animeabilities.client.util;

public class ColorUtil {
    public static ColorData unpackColor(int packedColor) {
        float alpha = (packedColor >> 24 & 255) / 255.0F;
        float r = (packedColor >> 16 & 255) / 255.0F;
        float g = (packedColor >> 8 & 255) / 255.0F;
        float b = (packedColor & 255) / 255.0F;
        return new ColorData(r, g, b, alpha);
    }

    public static class ColorData {
        private final float r;
        private final float g;
        private final float b;
        private final float alpha;

        private ColorData(float r, float g, float b, float alpha) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.alpha = alpha;
        }

        public float r() {
            return r;
        }

        public float g() {
            return g;
        }

        public float b() {
            return b;
        }

        public float alpha() {
            return alpha;
        }
    }
}
