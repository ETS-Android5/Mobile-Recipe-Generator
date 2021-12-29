import torch
import torch.nn as nn
import torch.optim as optim
from torch.optim import lr_scheduler

import numpy as np
import seaborn as sns

import torchvision
import matplotlib.pyplot as plt
from torchvision import datasets, models, transforms
import torch.onnx

import matplotlib.pyplot as plt

from PIL import Image

import time
import os
import copy

'''
RESOURCES:
PyTorch Documentation: https://pytorch.org/tutorials/beginner/transfer_learning_tutorial.html
Transfer Learning Example: https://www.youtube.com/watch?v=K0lWSB2QoIQ&t=6s
'''

# Predetermined values outlined in PyTorch documents
mean = np.array([0.485, 0.456, 0.406])
std = np.array([0.229, 0.224, 0.225])

# Allocate training device - GPU if available
device = "cpu"

# Apply transformations to input data
dataTransforms = {
    # Specific transforms for train.
    'train': transforms.Compose([
        transforms.RandomResizedCrop(224),
        transforms.RandomHorizontalFlip(),
        transforms.ToTensor(),
        transforms.Normalize(mean, std)
    ]),
    # Specific transforms for val.
    'val': transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(224),
        transforms.ToTensor(),
        transforms.Normalize(mean, std)
    ]),
}

# Directory of dataset
data_dir = 'data/Dataset'

# Specific directories for 'train' and 'val' set
image_datasets = {x: datasets.ImageFolder(os.path.join(data_dir, x),
                                          dataTransforms[x])
                  for x in ['train', 'val']}

# DataLoader creation for 'train' and 'val'
data_loaders = {x: torch.utils.data.DataLoader(image_datasets[x], batch_size = 128,
                                               shuffle = True, num_workers = 0)
                for x in ['train', 'val']}

# Info of data - size of dataset and number of classes
dataset_sizes = {x: len(image_datasets[x]) for x in ['train', 'val']}
class_names = image_datasets['train'].classes

# Output class names for debugging
print(class_names)

inputs, classes = next(iter(data_loaders['train']))

train_list_loss, val_list_loss, train_list_acc, val_list_acc = [], [], [], []


# Iterate through model, training and testing network on dataset provided
def train_model(model, criterion, optimizer, scheduler, num_epochs = 25):
    since = time.time()

    best_model_wts = copy.deepcopy(model.state_dict())
    best_acc = 0.0

    # Iterate for number of epochs given
    for epoch in range(num_epochs):
        # Output current input
        print('Epoch {}/{}'.format(epoch, num_epochs - 1))
        print('-' * 10)

        for phase in ['train', 'val']:
            # Change model state depending on phase being ran
            if phase == 'train':
                model.train()
            else:
                model.eval()

            running_loss = 0.0
            running_corrects = 0

            # Iterate through data input
            for inputs, labels in data_loaders[phase]:
                inputs = inputs.to(device)
                labels = labels.to(device)

                with torch.set_grad_enabled(phase == 'train'):
                    outputs = model(inputs)
                    _, preds = torch.max(outputs, 1)
                    loss = criterion(outputs, labels)

                    if phase == 'train':
                        optimizer.zero_grad()
                        loss.backward()
                        optimizer.step()

                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)

            if phase == 'train':
                scheduler.step()

            # Calculate loss and accuracy, then output values.
            epoch_loss = running_loss / dataset_sizes[phase]
            epoch_acc = running_corrects.double() / dataset_sizes[phase]

            if phase == 'train':
                train_list_loss.append(epoch_loss)
                train_list_acc.append(epoch_acc)
            else:
                val_list_loss.append(epoch_loss)
                val_list_acc.append(epoch_acc)

            print('{} Loss: {:.4f} Acc: {:.4f}'.format(phase, epoch_loss, epoch_acc))

            if phase == 'val' and epoch_acc > best_acc:
                best_acc = epoch_acc
                best_model_wts = copy.deepcopy(model.state_dict())

        print()

    # Calculate time taken for entire training (all epochs), then output best accuracy.
    time_elapsed = time.time() - since
    print('Training complete in {:.0f}m {:.0f}s'.format(time_elapsed // 60, time_elapsed % 60))
    print('Best val ACC: {:4f}'.format(best_acc))

    # Load highest accuracy model weights into state dict, then return updated model object.
    model.load_state_dict(best_model_wts)
    return model


# Import pre-trained ResNet model - trained on ImageNet dataset.
model = models.resnet34(pretrained=True)

# Freeze entire network (except final layer), preventing all parameters being changed.
for param in model.parameters():
    param.requires_grad = False

# Replace the last fully connected layer, declaring the number of inputs and output (108 = num of classes).
num_ftrs = model.fc.in_features
model.fc = nn.Linear(num_ftrs, 62)

# Ensure model is on current device (CPU or GPU).
model = model.to("cpu")

criterion = nn.CrossEntropyLoss()
optimizer = optim.SGD(model.parameters(), lr=0.001, momentum=0.9)

# Delay learning rate - multiply current lr by gamma every 7 epochs.
model_lr_scheduler = lr_scheduler.StepLR(optimizer, step_size=7, gamma=0.1)

# Start CNN training
model = train_model(model, criterion, optimizer, model_lr_scheduler, num_epochs=10)

# Ensure network is set to eval() mode before trying to save model
model.eval()

# Save as a .pt file.
torch.save(model, './data/CNN/ResnetFruits360CNNModified2021.pt')
