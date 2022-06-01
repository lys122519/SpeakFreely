package com.sf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@TableName("tb_files")
@ApiModel(value = "Files对象", description = "")
public class Files implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("id")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("文件名称")
      private String name;

      @ApiModelProperty("文件类型")
      private String type;

      @ApiModelProperty("文件大小 kb	")
      private Long size;

      @ApiModelProperty("文件url")
      private String url;

      @ApiModelProperty("文件md5")
        private String md5;

      @ApiModelProperty("是否已删除")
      @TableLogic
      private Boolean deleted;

      @ApiModelProperty("是否禁用")
      private Boolean enabled;

    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public String getName() {
        return name;
    }

      public void setName(String name) {
          this.name = name;
      }
    
    public String getType() {
        return type;
    }

      public void setType(String type) {
          this.type = type;
      }
    
    public Long getSize() {
        return size;
    }

      public void setSize(Long size) {
          this.size = size;
      }
    
    public String getUrl() {
        return url;
    }

      public void setUrl(String url) {
          this.url = url;
      }
    
    public String getMd5() {
        return md5;
    }

      public void setMd5(String md5) {
          this.md5 = md5;
      }
    
    public Boolean getDeleted() {
        return deleted;
    }

      public void setDeleted(Boolean deleted) {
          this.deleted = deleted;
      }
    
    public Boolean getEnabled() {
        return enabled;
    }

      public void setEnabled(Boolean enabled) {
          this.enabled = enabled;
      }

    @Override
    public String toString() {
        return "Files{" +
              "id=" + id +
                  ", name=" + name +
                  ", type=" + type +
                  ", size=" + size +
                  ", url=" + url +
                  ", md5=" + md5 +
                  ", deleted=" + deleted +
                  ", enabled=" + enabled +
              "}";
    }
}
