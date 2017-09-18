# VerifyView
Android端验证码视图

使用
```
  <com.mazaiting.VerifyView
      android:id="@+id/verifyView"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      app:codeTextSize="20sp"
      app:codeBackground="@color/colorAccent"
      app:codeLength="6"
      app:isContainChar="true"
      app:pointNum="200"
      app:lineNum="5"
      />
```
其中
	codeTextSize设置图片中验证码中的数字或字符大小<Br>
	codeBackground设置验证码背景图片<Br>
	codeLength设置验证长度<Br>
	isContainChar设置是否包含英文字母，true表示包含，false表示不包含<Br>
	pointNum设置干扰点的个数<Br>
	lineNum设置干扰线的条数<Br>

支持点击刷新验证码，判断验证码是否相等支持是否忽略大小写。<Br>
isEqualsIgnoreCase(String codeString)--忽略大小写<Br>
isEquals(String codeString)--不忽略大小写<Br>






