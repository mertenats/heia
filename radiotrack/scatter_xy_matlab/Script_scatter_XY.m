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
* Purpose:      Scatter XY, avg, std, ...
*
* Author:       Samuel M.
* Date:         22.06.2016
*
%}

% link to the file
% Excel file: worksheets, which name matchs 'tag1', 'tag2', ...
% Excel file: data: column 3: x coordinates, column 4: y coordinates
FILENAME = '/Users/mertenats/Desktop/Mesures/c1016_tag/jour/M4_jour_1120_1320_L12345_9personnes.xlsx';

% information about the graphic
title('Locators 2 and 5 - At night-time');

xlabel('X Coordinate [m]');
ylabel('Y Coordinate [m]');

A = 5; % size of each measurement
A_TAG = 10; % size of each tag
A_LOCATOR = 30;
C_TAG = 'r'; % color of the tags
C_LOCATOR = 'k';

hold on; % use the same plot for all the tags
grid minor; % draw a grid in background
axis equal;

% locators, only the locators 1, 2 and 5 are represented on the graph
% locator label, x coordinate, y coordinate
L = cell(3, 3);
L{1, 1} = 'L1 (-362,272)';
L{1, 2} = -3.62;
L{1, 3} = 2.72;

L{2, 1} = 'L2 (362,272)';
L{2, 2} = 3.62;
L{2, 3} = 2.72;

L{3, 1} = 'L5 (0,87)';
L{3, 2} = 0;
L{3, 3} = 0.87;
disp(L);

% tags, represented with an array
% tag name, x coordinate, y coordinate, representative color, label
T = cell(5, 5);
T{1, 1} = 'tag1';
T{1, 2} = 0; % x coordinate
T{1, 3} = 0.87; % y coordinate
T{1, 4} = 'c'; % color
T{1, 5} = 'T1 (0,87)'; % label

T{2, 1} = 'tag2';
T{2, 2} = 1.57;
T{2, 3} = 0.87;
T{2, 4} = 'g';
T{2, 5} = 'T2 (157,87)';

T{3, 1} = 'tag3';
T{3, 2} = -0.15;
T{3, 3} = 5.1;
T{3, 4} = 'y';
T{3, 5} = 'T3 (-15,510)';

T{4, 1} = 'tag4';
T{4, 2} = -1.07;
T{4, 3} = 4.39;
T{4, 4} = 'g';
T{4, 5} = 'T4 (-107,439)';


T{5, 1} = 'tag5';
T{5, 2} = -4.14;
T{5, 3} = 2.65;
T{5, 4} = 'c';
T{5, 5} = 'T5 (-414,265)';
disp(T);

% loop through the locators
for i = 1:length(L);
    % add label to the locator
    text(L{i, 2} + 0.15, L{i, 3} + 0.15, L{i, 1}, 'FontSize', 6, 'Color', C_LOCATOR);
    % draw the locator in black, with a bigger size
    scatter(L{i, 2}, L{i, 3}, A_LOCATOR,'filled', C_LOCATOR);
end

% loop through the worksheets of the document (Excel)
for i = 1:length(T);
    % load the specific worksheet
    % one worksheet for each tag
    data = xlsread(FILENAME, T{i, 1});
    x = data(:,3); % get x
    y = data(:,4); % get y
    p = data(:,6); % get the precision computed by Quuppa

    output = [T{i, 1}];
    disp(output);
    output = ['-------------------------------------------------------'];
    disp(output);

    % number of measurements
    output = ['MEASUREMENTS: ', num2str(length(x))];
    disp(output);

    % average of x and y
    avg_x = mean(x);
    avg_y = mean(y);
    output = ['AVG X: ', num2str(avg_x * 100), ' [cm] --- AVG Y: ', num2str(avg_y * 100), ' [cm]'];
    disp(output);

    % min/max of x and y
    %output = ['MIN X: ', num2str(min(x) * 100), ' [cm] MAX X: ', num2str(max(x) * 100), ' [cm] --- MIN Y: ', num2str(min(y) * 100), ' [m] MAX Y: ', num2str(max(y) * 100), ' [m]'];
    %disp(output);

    % standard deviation: http://ch.mathworks.com/help/matlab/ref/std.html#bune77u
    % When w = 0 (default), S is normalized by N-1.
    std_x = std(x, 0);
    std_y = std(y, 0);
    output = ['STD X: ', num2str(std_x * 100), ' [cm] --- STD Y: ', num2str(std_y * 100), ' [cm]'];
    disp(output);

    % variance: http://ch.mathworks.com/help/matlab/ref/var.html
    % When w = 0 (default), V is normalized by the number of observations-1
    % output = ['VAR X: ', num2str(var(x, 0)), ' [m] VAR Y: ', num2str(var(y, 0)), ' [m]'];
    % disp(output);

    % absolute error, x coordinate
    abs_error_x = abs(T{i, 2} - avg_x);
    output = ['ABSOLUTE ERROR X: ', num2str(abs_error_x * 100), ' [cm]'];
    disp(output);

    % absolute error, y coordinate
    abs_error_y = abs(T{i, 3} - avg_y);
    output = ['ABSOLUTE ERROR Y: ', num2str(abs_error_y * 100), ' [cm]'];
    disp(output);

    % absolute error, (x, y)
    abs_error_xy = sqrt(abs_error_x.^2 + abs_error_y.^2);
    output = ['ABSOLUTE ERROR (X, Y): ', num2str(abs_error_xy * 100), ' [cm]'];
    disp(output);

    output = ['QUUPPA ERROR AVG: ', num2str(mean(p) * 100), ' [cm]'];
    disp(output);
    disp(char(10)); % line break

    % draw the measurements on the graphic
    scatter(x, y, A, 'filled', T{i, 4});
    % add label to the tag
    text(T{i, 2} + 0.15, T{i, 3} - 0.15, T{i, 5}, 'FontSize', 6, 'Color', C_TAG);
    % draw the tag in red, with a bigger size
    scatter(T{i, 2}, T{i, 3}, A_TAG,'filled', C_TAG);

    % draw a line between the average and +/- the deviation
    x = [avg_x - std_x, avg_x + std_x];
    y = [avg_y, avg_y];
    plot(x, y, 'LineStyle', '-', 'Color', 'k', 'LineWidth', 0.5);
    x = [avg_x, avg_x];
    y = [avg_y - std_y, avg_y + std_y];
    plot(x, y, 'LineStyle', '-', 'Color', 'k', 'LineWidth', 0.5);
end
