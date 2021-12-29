import io

import torch

import torchvision.transforms as transforms
from PIL import Image
from flask import Flask, request, jsonify


app = Flask(__name__)

class_types = ['Apple Braeburn', 'Apple Crimson Snow', 'Apple Granny Smith', 'Apple Pink Lady', 'Apple Red 1',
               'Apricot', 'Avocado', 'Avocado ripe', 'Banana', 'Banana Lady Finger', 'Beetroot',
               'Blueberry', 'Cauliflower', 'Cherry 1', 'Cherry 2', 'Cherry Rainier', 'Cherry Wax Black',
               'Cherry Wax Red', 'Clementine', 'Cocos', 'Corn', 'Eggplant', 'Grape Blue', 'Grape Pink', 'Grape White',
               'Grape White 2', 'Green Bell Pepper', 'Kiwi', 'Lemon', 'Limes', 'Mango', 'Mango Red',
               'Orange', 'Orange Bell Pepper', 'Peach', 'Peach Flat', 'Pear', 'Pear Monster', 'Pear Williams',
               'Pineapple', 'Pineapple Mini', 'Plum', 'Plum 2', 'Plum 3', 'Pomegranate', 'Raspberry',
               'Red Bell Pepper', 'Red Onion', 'Red Potato', 'Strawberry', 'Strawberry Wedge', 'Sweet Potato',
               'Tomato 1', 'Tomato 2', 'Tomato 3', 'Tomato 4', 'Tomato Cherry Red', 'Tomato Heart', 'Tomato Yellow',
               'Tomato not Ripened', 'Watermelon', 'White Onion', 'White Potato', 'Yellow Bell Pepper']

model = torch.load('./data/CNN/ResnetFruits360CNNModifiedNoNorm.pt')
model.to("cpu")
model.eval()


@app.route('/predict', methods=['POST'])
def predict():
    if request.method == 'POST':

        image_file = request.files['image0']
        img_bytes = image_file.read()

        class_name = get_prediction(image_bytes=img_bytes)

        return class_name


def transform_image(image_bytes):
    my_transforms = transforms.Compose([transforms.Resize(256),
                                        transforms.CenterCrop(224),
                                        transforms.ToTensor(),
                                        ])

    image = Image.open(io.BytesIO(image_bytes))
    return my_transforms(image).unsqueeze(0)


def get_prediction(image_bytes):
    tensor = transform_image(image_bytes=image_bytes)

    prob = torch.exp(model.forward(tensor))
    _, top_labs = prob.topk(5)
    top_labs = top_labs.detach().numpy().tolist()[0]

    top_classes = [None] * 5
    for i in range(len(top_labs)):
        top_classes[i] = class_types[top_labs[i]]

    print(top_classes)
    return jsonify(top_classes)


if __name__ == '__main__':
    app.run(host='0.0.0.0')
