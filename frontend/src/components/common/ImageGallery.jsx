import { Box, Grid } from "@mui/material";
import { useEffect, useState } from "react";

function ImageGallery({ images = [] }) {
  const [selectedImage, setSelectedImage] = useState(null);

  // images가 바뀔 때 한 번만 대표 이미지 선택
  useEffect(() => {
    if (images.length === 0) return;

    const mainImg = images.find(img => img.isMain) || images[0];
    setSelectedImage(mainImg);
  }, [images]);


  return (
    <>
      {selectedImage && (
        <Box
          component="img"
          src={selectedImage.url}
          alt={selectedImage.originalName}
          sx={{ width: '100%', height: 400, objectFit: 'contain', mb: 2 }}
        />
      )}

      {/* 사진 목록 */}
      <Grid container spacing={1}>
        {images.map((img) => (
          <Grid key={img.id}>
            <Box
              component="img"
              src={img.url}
              alt={img.originalName}
              sx={{
                width: 80,
                height: 80,
                objectFit: 'cover',
                border: img === selectedImage ? '2px solid #007FFF' : '1px solid #ccc',
                cursor: 'pointer',
              }}
              onClick={() => setSelectedImage(img)}
            />
          </Grid>
        ))}
      </Grid>
    </>
  );
}

export default ImageGallery; 