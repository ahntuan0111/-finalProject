import os
import re

directory = r"e:\Final_Project\finalProject_Mobileapp\app\src\main\java\com\example\finalproject\ui\screens"
vietnamese_chars = "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ"
pattern = re.compile(rf'"([^"\\]*?[{vietnamese_chars}]+[^"\\]*?)"')

for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r", encoding="utf8") as f:
                content = f.read()
                matches = pattern.findall(content)
                if matches:
                    print(f"File: {os.path.basename(path)}")
                    for m in matches:
                        print(f" - {m}")
