%{
* Copyright 2016 University of Applied Sciences Western Switzerland / Fribourg
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Project:      HEIA-FR / RadioTrack
*
* Abstract:     Script to plot a scatter XY and compute statistics
*               for a given Excel file
*
* Purpose:      Scatter XYZ, min, max, avg, ...
*
* Author:       Samuel M.
* Date:         30.06.2016
*
%}

% link to the file
% Excel file: worksheets, which name matchs 'tag1', 'tag2', ...
% Excel file: data: column 3: x coordinates, column 4: y coordinates
FILENAME = '/Users/mertenats/Desktop/Mesures/c1004_tag_3d/c1004_3d_800_0900_1h_location_20160630_090147.xlsx';

A = 5; % size of each measurement
A_TAG = 10; % size of each tag
A_LOCATOR = 30;
C_TAG = 'r'; % color of the tags
C_LOCATOR = 'k';

% locators, only the locators 1, 2 and 5 are represented on the graph
% locator label, x coordinate, y coordinate
L = cell(3, 4);
L{1, 1} = 'L1 (0, 292, 208)';
L{1, 2} = 0; % x
L{1, 3} = 2.92; % y
L{1, 4} = 2.08; % z

L{2, 1} = 'L2 (-372, -5, 207)';
L{2, 2} = -3.72;
L{2, 3} = -0.05;
L{2, 4} = 2.07;

L{3, 1} = 'L3 (372, 2, 207)';
L{3, 2} = 3.72;
L{3, 3} = 0.02;
L{3, 4} = 2.07;
disp(L);

% tags, represented with an array
% tag name, x coordinate, y coordinate, representative color, label
T = cell(4, 6);
T{1, 1} = 'tag2';
T{1, 2} = 0.06; % x coordinate
T{1, 3} = -2.39; % y coordinate
T{1, 4} = 'c'; % color
T{1, 5} = 'T1 (6, -239, 0)'; % label
T{1, 6} = 0; % z coordinate

T{2, 1} = 'tag4';
T{2, 2} = 0.06;
T{2, 3} = -3.5;
T{2, 4} = 'g';
T{2, 5} = 'T2 (6, -350, 200)';
T{2, 6} = 2;

T{3, 1} = 'tag3';
T{3, 2} = 1.27;
T{3, 3} = -1;
T{3, 4} = 'c';
T{3, 5} = 'T3 (127, -100, 100)';
T{3, 6} = 1; % z coordinate

T{4, 1} = 'tag1';
T{4, 2} = -1.77;
T{4, 3} = -3.25;
T{4, 4} = 'g';
T{4, 5} = 'T4 (-177, -325, 150)';
T{4, 6} = 1.5;
disp(T);

% loop through the worksheets of the document (Excel)
for i = 1:size(T);
    % load the specific worksheet
    % one worksheet for each tag
    data = xlsread(FILENAME, T{i, 1});
    x = data(:,3); % get x
    y = data(:,4); % get y
    z = data(:,5); % get z
    p = data(:,6); % get the precision computed by Quuppa

    output = [T{i, 5}];
    disp(output);
    output = ['-------------------------------------------------------'];
    disp(output);

    % number of measurements
    output = ['MEASUREMENTS: ', num2str(length(x))];
    disp(output);

    % average of x and y
    avg_x = mean(x);
    avg_y = mean(y);
    avg_z = mean(z);
    output = ['AVG X: ', num2str(avg_x * 100), ' [cm] --- AVG Y: ', num2str(avg_y * 100), ' [cm] --- AVG Z: ', num2str(avg_z * 100) ' [cm]'];
    disp(output);

    % standard deviation: http://ch.mathworks.com/help/matlab/ref/std.html#bune77u
    % When w = 0 (default), S is normalized by N-1.
    std_x = std(x, 0);
    std_y = std(y, 0);
    std_z = std(z, 0);
    output = ['STD X: ', num2str(std_x * 100), ' [cm] --- STD Y: ', num2str(std_y * 100), ' [cm] --- STD Z: ', num2str(std_z * 100), ' [cm]'];
    disp(output);

    % absolute error, x coordinate
    abs_error_x = abs(T{i, 2} - avg_x);
    output = ['ABSOLUTE ERROR X: ', num2str(abs_error_x * 100), ' [cm]'];
    disp(output);

    % absolute error, y coordinate
    abs_error_y = abs(T{i, 3} - avg_y);
    output = ['ABSOLUTE ERROR Y: ', num2str(abs_error_y * 100), ' [cm]'];
    disp(output);

    % absolute error, z coordinate
    abs_error_z = abs(T{i, 6} - avg_z);
    output = ['ABSOLUTE ERROR Z: ', num2str(abs_error_z * 100), ' [cm]'];
    disp(output);

    % absolute error, (x, y, z)
    abs_error_xyz = sqrt(abs_error_x.^2 + abs_error_y.^2 + abs_error_z.^2);
    output = ['ABSOLUTE ERROR (X, Y, Z): ', num2str(abs_error_xyz * 100), ' [cm]'];
    disp(output);

    output = ['QUUPPA ERROR AVG: ', num2str(mean(p) * 100), ' [cm]'];
    disp(output);
    disp(char(10)); % line break

    % draw the measurements on the graphic
    scatter3(x, y, z, A, 'filled', T{i, 4});
    alpha(0.1);
    hold on;
    axis equal;

    % draw a line between the average and +/- the deviation
    x = [avg_x - std_x, avg_x + std_x];
    y = [avg_y, avg_y];
    z = [avg_z, avg_z];
    plot3(x, y, z, 'LineStyle', '-', 'Color', 'k', 'LineWidth', 0.75);
    x = [avg_x, avg_x];
    y = [avg_y - std_y, avg_y + std_y];
    z = [avg_z, avg_z];
    plot3(x, y, z, 'LineStyle', '-', 'Color', 'k', 'LineWidth', 0.75);
    x = [avg_x, avg_x];
    y = [avg_y, avg_y];
    z = [avg_z - std_z, avg_z + std_z];
    plot3(x, y, z, 'LineStyle', '-', 'Color', 'k', 'LineWidth', 0.75);

    % information about the graphic
    title('Locators 1-3 - 3D - At day-time');
    xlabel('X Coordinate [m]');
    ylabel('Y Coordinate [m]');
    zlabel('Z Coordinate [m]');
end

% loop through the locators
for i = 1:size(L);
    % add label to the locator
    text(L{i, 2} + 0.15, L{i, 3} + 0.15, L{i, 4} + 0.15, L{i, 1}, 'FontSize', 6, 'Color', C_LOCATOR);
    % draw the locator in black, with a bigger size
    scatter3(L{i, 2}, L{i, 3}, L{i, 4}, A_LOCATOR,'filled', C_LOCATOR);
end

% loop through the tags
for i = 1:size(T);
    % add label to the tag
    text(T{i, 2} + 0.15, T{i, 3} - 0.15, T{i, 6} - 0.15, T{i, 5}, 'FontSize', 6, 'Color', C_TAG);
    % draw the tag in red, with a bigger size
    scatter3(T{i, 2}, T{i, 3}, T{i, 6}, A_TAG,'filled', C_TAG);
end
