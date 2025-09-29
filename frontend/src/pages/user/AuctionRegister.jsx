import { Box, Button, Container, FormControl, Grid, IconButton, InputLabel, MenuItem, Select, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import DeleteIcon from "@mui/icons-material/Delete";
import api from "../../api/api";

export default function AuctionRegister() {
  const {
    control,
    handleSubmit,
    setValue,
    formState: { isSubmitting, isSubmitted, errors },
  } = useForm({
    defaultValues: {
      title: '',
      categoryId: 1,
      startPrice: 1000,
      startTime: '',
      endTime: '',
      images: [] // 빈 배열 초기화
    }
  });

  const [isMain, setIsMain] = useState(0);
  const [categories, setCategories] = useState([]);

  const onSubmit = (data) => {
    const formData = new FormData();
    formData.append("title", data.title);
    formData.append("categoryId", data.categoryId);
    formData.append("description", data.description);
    formData.append("startPrice", data.startPrice);
    formData.append("startTime", data.startTime);
    formData.append("endTime", data.endTime);

    data.images.forEach((file, index) => {
      formData.append(`files[${index}].file`, file);
      formData.append(`files[${index}].sortOrder`, index);
      formData.append(`files[${index}].isMain`, index === isMain);
    });

    api.post("/api/auctions", formData, {
      headers: { "Content-Type": "multipart/form-data" }
    })
      .then(res => console.log(res))
      .catch(err => console.error(err));
  };

  useEffect(() => {
    api.get("/api/categories")
      .then(res => setCategories(res.data))
      .catch(() => alert("카테고리 불러오기 실패"));
  }, []);

  const flattenCategories = (categories, parentName = "") => {
    return categories.flatMap(cat => {
      const fullName = parentName ? `${parentName} > ${cat.name}` : cat.name;
      const items = [{ id: cat.id, name: fullName }];
      if (cat.children) {
        return [...items, ...flattenCategories(cat.children, fullName)];
      }
      return items;
    });
  }

  const flatCategoryList = flattenCategories(categories);

  return (
    <Container
      id="auctions"
      sx={{
        pt: { xs: 14, sm: 20 },
        pb: { xs: 8, sm: 12 },
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: { xs: 3, sm: 6 },
      }}
    >
      <form onSubmit={handleSubmit(onSubmit)}>
        {/* 제목 */}
        <Controller
          name="title"
          control={control}
          rules={{
            required: "제목은 필수입니다.",
            maxLength: {
              value: 50,
              message: "제목은 50자 이하로 입력해주세요."
            }
          }}
          render={({ field }) => (
            <TextField
              {...field}
              label="상품 제목"
              variant="outlined"
              fullWidth
              error={!!errors.title}
              helperText={errors.title?.message}
              sx={{ mb: 3 }}
            />
          )}
        />

        {/* 카테고리 */}
        {categories.length > 0 && (<Controller
          name="categoryId"
          control={control}
          rules={{ required: "카테고리를 선택하세요." }}
          render={({ field }) => (
            <FormControl fullWidth variant="outlined" sx={{ mb: 1 }} error={!!errors.categoryId}>
              <InputLabel>카테고리</InputLabel>
              <Select {...field} label="카테고리">
                {flatCategoryList.map(cat => (
                  <MenuItem key={cat.id} value={cat.id}>
                    {cat.name}
                  </MenuItem>
                ))}
              </Select>
              {errors.categoryId && (
                <Typography variant="caption" color="error">
                  {errors.categoryId.message}
                </Typography>
              )}
            </FormControl>
          )}
        />
        )}

        {/* 설명 */}
        <Controller
          name="description"
          control={control}
          rules={{ required: "상품 설명은 필수입니다." }}
          render={({ field }) => (
            <TextField
              {...field}
              label="상품 설명"
              variant="outlined"
              fullWidth
              multiline
              minRows={4}
              sx={{ mt: 2 }}
              error={!!errors.description}
              helperText={errors.description?.message}
            />
          )}
        />

        {/* 시작가 */}
        <Controller
          name="startPrice"
          control={control}
          rules={{
            required: "경매 시작가 입력",
            min: { value: 1000, message: "최소 1000원 이상 입력" },
            max: { value: 10000000, message: "최대 1000만원 이하 입력" },
          }}
          render={({ field }) => (
            <TextField
              {...field}
              label="시작가"
              type="number"
              variant="outlined"
              error={!!errors.startPrice}
              helperText={errors.startPrice?.message}
              sx={{ mt: 2 }}
            />
          )}
        />

        <Grid container spacing={2} sx={{ mt: 2 }}>
          {/* 시작 시간 */}
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="startTime"
              control={control}
              rules={{
                required: "시작 시간을 입력하세요.",
                validate: (v) => {
                  const start = new Date(v);
                  const tomorrow = new Date();
                  tomorrow.setHours(0, 0, 0, 0);
                  tomorrow.setDate(tomorrow.getDate() + 1);
                  return start >= tomorrow || "시작 시간은 내일 00시 이후여야 합니다.";
                }
              }}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="시작 시간"
                  type="datetime-local"
                  variant="outlined"
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.startTime}
                  helperText={errors.startTime?.message}
                />
              )}
            />
          </Grid>

          {/* 종료 시간 */}
          <Grid size={{ xs: 12, sm: 6 }}>
            <Controller
              name="endTime"
              control={control}
              rules={{
                required: "종료 시간을 입력하세요.",
                validate: (v, formValues) => {
                  const start = new Date(formValues.startTime);
                  const end = new Date(v);

                  if (end <= start) return "종료는 시작 이후여야 합니다.";
                  if (end > new Date(start.getTime() + 10 * 24 * 60 * 60 * 1000))
                    return "종료는 시작 후 10일 이내여야 합니다.";
                  return true;
                }
              }}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="종료 시간"
                  type="datetime-local"
                  variant="outlined"
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.endTime}
                  helperText={errors.endTime?.message}
                />
              )}
            />
          </Grid>
        </Grid>

        {/* 파일 업로드 */}
        <Controller
          name="images"
          control={control}
          rules={{ required: "이미지를 최소 1개 업로드하세요." }}
          render={({ field }) => {
            const handleFilesChange = (e) => {
              const files = Array.from(e.target.files);
              const newFiles = [...field.value, ...files].slice(0, 5);
              setValue("images", newFiles, { shouldValidate: true });
            };

            const handleDelete = (index) => {
              const newFiles = field.value.filter((_, i) => i !== index);
              setValue("images", newFiles, { shouldValidate: true });
              if (isMain >= newFiles.length) setIsMain(0);
            };

            return (
              <Box sx={{ mt: 2 }}>
                <input
                  type="file"
                  multiple
                  accept="image/*"
                  style={{ display: "none" }}
                  id="file-upload"
                  onChange={handleFilesChange}
                />
                <label htmlFor="file-upload">
                  <Button variant="contained" component="span">
                    이미지 선택
                  </Button>
                </label>

                {errors.images && (
                  <Typography color="error" variant="caption" display="block">
                    {errors.images.message}
                  </Typography>
                )}

                <Grid container spacing={2} sx={{ mt: 1 }}>
                  {field.value.map((file, index) => {
                    const url = URL.createObjectURL(file);
                    return (
                      <Grid key={index}>
                        <Box
                          sx={{
                            position: "relative",
                            border: isMain === index ? "2px solid #007FFF" : "1px solid #ccc",
                            borderRadius: 1,
                            width: 120,
                            height: 120,
                            overflow: "hidden",
                          }}
                        >
                          <img
                            src={url}
                            alt={`preview-${index}`}
                            style={{ width: "100%", height: "100%", objectFit: "cover", cursor: "pointer" }}
                            onClick={() => setIsMain(index)}
                          />
                          <IconButton
                            size="small"
                            onClick={() => handleDelete(index)}
                            sx={{ position: "absolute", top: 0, right: 0, color: "red" }}
                          >
                            <DeleteIcon fontSize="small" />
                          </IconButton>
                        </Box>
                        <Typography variant="caption" display="block" textAlign="center">
                          {isMain === index ? "대표" : ``}
                        </Typography>
                      </Grid>
                    )
                  })}
                </Grid>
              </Box>
            );
          }}
        />


        <Button sx={{ mt: 2 }} type="submit" variant="contained" color="primary" fullWidth disabled={isSubmitting}>
          등록
        </Button>
      </form>
    </Container>
  )
}