ReceiveTakeSSRequestCommand = {
    defaultName = 'receiveTakeSSRequest',
}

if not isClient() then
    return
end

ReceiveTakeSSRequestCommand.execute = function(package)
    if package.playerOnlineId ~= nil then
        print("SALDBG Client EXECUTE: Module:" .. module .. "playerid: " .. package.playerOnlineId .. " command: " .. command  .. " args: " .. args)
        
        --sendClientCommand(package.playerOnlineId, 'saldiscord', CheckBloodPressureCommand.defaultName, package)
    end
end

Events.OnServerCommand.Add(
    function(module, command, package)
        if module == 'saldiscord' and command == ReceiveTakeSSRequestCommand.defaultName then
            print("SALDBG Client COMMAND: Module:" .. module .. " command: " .. command  .. " args: " .. args)
            ReceiveTakeSSRequestCommand.execute(package)
        end
    end
)
